"""ローカルで動かす文書AI読取サービス。

Spring BootからPDFまたは画像を受け取り、Ollamaの画像対応モデルへ渡す。
このサービスは127.0.0.1だけで待ち受け、外部には公開しない。
"""

import base64
import json
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
MAX_FILE_SIZE = 50 * 1024 * 1024


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
        candidates = call_ollama(image, create_prompt(document_type, prime_constractor_id))
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
キーは customerName, mobilePhone, address, itemModel1, itemModel2, requestedDate, contactNote, items, works です。
items は商品の配列です。各要素のキーは itemName, itemModel, itemQuantity です。
works はリサイクル運搬など、伝票に記載された作業項目の配列です。各要素のキーは orderWorkName, orderWorkQuantity, orderWorkPrice です。
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


def call_ollama(image_path: Path, prompt: str) -> dict[str, str]:
    payload = {
        "model": OLLAMA_MODEL,
        "stream": False,
        "format": "json",
        "options": {"num_ctx": 8192},
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
        content = body["message"]["content"]
        parsed = json.loads(content)
    except (urllib.error.URLError, TimeoutError, KeyError, TypeError, json.JSONDecodeError) as error:
        raise HTTPException(status_code=502, detail="ローカルAIモデルから読取結果を取得できませんでした。") from error
    if not isinstance(parsed, dict):
        raise HTTPException(status_code=502, detail="ローカルAIモデルの結果形式が不正です。")
    return {str(key): "" if value is None else json.dumps(value, ensure_ascii=False) if isinstance(value, (list, dict)) else str(value) for key, value in parsed.items()}
