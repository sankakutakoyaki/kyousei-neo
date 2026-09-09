"""ローカルで動かす文書AI読取サービス。

Spring BootからPDFまたは画像を受け取り、Ollamaの画像対応モデルへ渡す。
このサービスは127.0.0.1だけで待ち受け、外部には公開しない。
"""

import base64
import json
import logging
import os
import subprocess
import tempfile
import urllib.error
import urllib.request
from pathlib import Path

from fastapi import FastAPI, File, Form, HTTPException, UploadFile


app = FastAPI(title="Kyousei Document AI Reader", version="0.1.0")

OLLAMA_URL = os.getenv("AI_READER_OLLAMA_URL", "http://127.0.0.1:11434/api/chat")
OLLAMA_MODEL = os.getenv("AI_READER_OLLAMA_MODEL", "qwen2.5vl:3b")
PDFTOPPM_COMMAND = os.getenv("AI_READER_PDFTOPPM_COMMAND", "pdftoppm")
logger = logging.getLogger(__name__)
MAX_FILE_SIZE = 50 * 1024 * 1024
OLLAMA_NUM_CTX = int(os.getenv("AI_READER_NUM_CTX", "16384"))


@app.get("/health")
def health() -> dict[str, str]:
    """プロセス起動確認用。AIモデルの稼働確認は読取実行時に行う。"""
    return {"status": "ok", "model": OLLAMA_MODEL}


@app.post("/v1/document-extractions")
async def extract_document(
    file: UploadFile = File(...),
    document_type: str = Form(..., alias="documentType"),
    prime_constractor_id: str | None = Form(None, alias="primeConstractorId"),
) -> dict[str, dict[str, str]]:
    if document_type not in {"ORDER_FAX", "EXPENSE_RECEIPT"}:
        raise HTTPException(status_code=400, detail="未対応の文書種別です。")

    content = await file.read()
    if not content or len(content) > MAX_FILE_SIZE:
        raise HTTPException(status_code=400, detail="ファイルが空か、サイズが大きすぎます。")

    suffix = Path(file.filename or "document").suffix.lower()
    if suffix not in {".pdf", ".png", ".jpg", ".jpeg"}:
        raise HTTPException(status_code=400, detail="PDF、PNG、JPEGのみ読み取れます。")

    with tempfile.TemporaryDirectory(prefix="kyousei-ai-reader-") as temporary_directory:
        source = Path(temporary_directory, "source" + suffix)
        source.write_bytes(content)
        image = render_first_page(source) if suffix == ".pdf" else source
        candidates = call_ollama(image, create_prompt(document_type, prime_constractor_id), document_type)
    return {"candidates": candidates}


def render_first_page(pdf_path: Path) -> Path:
    image_prefix = pdf_path.with_name("page")
    try:
        subprocess.run(
            [PDFTOPPM_COMMAND, "-r", "200", "-f", "1", "-singlefile", "-png", str(pdf_path), str(image_prefix)],
            check=True,
            capture_output=True,
            text=True,
            timeout=60,
        )
    except (OSError, subprocess.SubprocessError) as error:
        raise HTTPException(status_code=500, detail="PDFを画像へ変換できませんでした。") from error
    image = image_prefix.with_suffix(".png")
    if not image.is_file():
        raise HTTPException(status_code=500, detail="PDFの1ページ目を取得できませんでした。")
    return image


def create_prompt(document_type: str, prime_constractor_id: str | None) -> str:
    if document_type == "ORDER_FAX":
        prompt = """これは日本語のFAX受注伝票です。手書きや印字を読み取り、次のJSONだけを返してください。
キーは customerName, mobilePhone, postalCode, address, itemModel1, itemModel2, requestedDate, contactNote, items, works です。
postalCode は配送工事先の郵便番号です。伝票に記載がある場合だけ読み取り、123-4567形式で返してください。荷主・店舗の郵便番号は使わず、不明な場合は空文字列にしてください。住所から推測しないでください。
items は商品の配列です。各要素のキーは itemName, itemModel, itemQuantity です。
works はリサイクル運搬など、伝票に記載された作業項目の配列です。各要素のキーは orderWorkName, orderWorkQuantity, orderWorkPrice です。
小計・消費税・合計・総額は作業項目ではないため works に含めないでください。
同じ明細を繰り返し出力せず、伝票の各明細行につき1要素だけ返してください。各値は簡潔な文字列で返してください。
orderWorkPrice は作業の単価です。合計金額から計算しないでください。
商品や作業がなければ空配列にしてください。名称・数量・単価を推測しないでください。不明な項目は空文字列にしてください。
requestedDate に年が明記されていれば YYYY-MM-DD で返し、年が不明なら年を推測しないでください。
不明または自信がない値は空文字列にしてください。値を推測・補完しないでください。
説明文、Markdown、コードブロックは付けないでください。"""
        if (prime_constractor_id or "").strip() == "1085":
            prompt += """
この伝票は平和堂の受注伝票です。
requestedDate は、帳票の「工事希望日」欄に記載された日付だけを読み取ってください。
「受付日」「発行日」「注文日」など、別の欄の日付を requestedDate に使わないでください。
「工事希望日」の見出しに対応する欄を確認し、その欄の記載を読み取ってください。
「工事希望日」欄が見つからない、空欄、または判読できない場合は、requestedDate を空文字列にしてください。
他の日付から推測・補完しないでください。"""
        return prompt
    return """これは日本語の領収書またはレシートです。次のJSONだけを返してください。
キーは storeName, receiptDate, totalAmount, taxAmount, paymentMethod, description です。
不明または自信がない値は空文字列にしてください。金額は数字だけにしてください。
説明文、Markdown、コードブロックは付けないでください。"""


def response_schema(document_type: str) -> dict:
    def object_schema(keys):
        return {"type": "object", "properties": {key: {"type": "string"} for key in keys},
                "required": list(keys), "additionalProperties": False}

    if document_type == "ORDER_FAX":
        schema = object_schema(["customerName", "mobilePhone", "postalCode", "address",
                                "itemModel1", "itemModel2", "requestedDate", "contactNote"])
        for key, fields in {
            "items": ["itemName", "itemModel", "itemQuantity"],
            "works": ["orderWorkName", "orderWorkQuantity", "orderWorkPrice"],
        }.items():
            schema["properties"][key] = {"type": "array", "items": object_schema(fields)}
            schema["required"].append(key)
        return schema
    return object_schema(["storeName", "receiptDate", "totalAmount", "taxAmount", "paymentMethod", "description"])


def matches_schema(value, schema: dict) -> bool:
    if schema["type"] == "string":
        return isinstance(value, str)
    if schema["type"] == "array":
        return isinstance(value, list) and all(matches_schema(row, schema["items"]) for row in value)
    return (isinstance(value, dict) and set(value) == set(schema["properties"])
            and all(matches_schema(value[key], child) for key, child in schema["properties"].items()))


def call_ollama(image_path: Path, prompt: str, document_type: str = "ORDER_FAX") -> dict[str, str]:
    payload = {
        "model": OLLAMA_MODEL,
        "stream": False,
        "format": response_schema(document_type),
        "options": {"num_ctx": OLLAMA_NUM_CTX, "temperature": 0},
        "messages": [{
            "role": "user",
            "content": prompt,
            "images": [base64.b64encode(image_path.read_bytes()).decode("ascii")],
        }],
    }
    request = urllib.request.Request(
        OLLAMA_URL,
        data=json.dumps(payload).encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    try:
        with urllib.request.urlopen(request, timeout=180) as response:
            body = json.loads(response.read().decode("utf-8"))
    except (urllib.error.URLError, TimeoutError, UnicodeDecodeError, json.JSONDecodeError) as error:
        logger.warning("Ollama request failed: %s", type(error).__name__)
        raise HTTPException(status_code=502, detail="ローカルAIモデルとの通信に失敗しました。AIサービスのログを確認してください。") from error
    if not isinstance(body, dict):
        raise HTTPException(status_code=502, detail="ローカルAIモデルの応答形式が不正です。")
    logger.info("Ollama completion: done=%s reason=%s input_tokens=%s output_tokens=%s",
                body.get("done"), body.get("done_reason"), body.get("prompt_eval_count"), body.get("eval_count"))
    if body.get("done_reason") == "length" or body.get("done") is False:
        logger.warning("Ollama response incomplete: context=%s reason=%s input_tokens=%s output_tokens=%s",
                       OLLAMA_NUM_CTX, body.get("done_reason"), body.get("prompt_eval_count"), body.get("eval_count"))
        raise HTTPException(status_code=502, detail="AIの読取結果が途中で打ち切られました。再度読み込んでください。繰り返す場合はAIサービスのログを確認してください。")
    try:
        parsed = json.loads(body["message"]["content"])
    except (KeyError, TypeError, json.JSONDecodeError) as error:
        logger.warning("Ollama result is invalid JSON: reason=%s output_tokens=%s",
                       body.get("done_reason"), body.get("eval_count"))
        raise HTTPException(status_code=502, detail="AIの読取結果を解析できませんでした。返答が途中で切れた可能性があります。再度読み込んでください。") from error
    if not matches_schema(parsed, response_schema(document_type)):
        logger.warning("Ollama result does not match document schema")
        raise HTTPException(status_code=502, detail="ローカルAIモデルの結果項目が不正です。再度読み込んでください。")
    return {key: json.dumps(value, ensure_ascii=False) if isinstance(value, list) else value
            for key, value in parsed.items()}
