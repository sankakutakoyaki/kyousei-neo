# 文書AI読取サービスの接続仕様

GPU導入後、Spring Bootとは別プロセスとして文書AI読取サービスを起動する。
現在の受注システムは、`order.ai.enabled=false` の間は従来のTesseract OCRを利用する。

## 接続先

`POST http://127.0.0.1:18080/v1/document-extractions`

同じUbuntuサーバー内だけで通信する。外部ネットワークには公開しない。

## リクエスト

`multipart/form-data` で以下を送信する。

| 項目 | 内容 |
| --- | --- |
| `file` | 取り込み済みのPDFファイル |
| `primeConstractorId` | 荷主ID |
| `documentType` | `ORDER_FAX`。将来は`EXPENSE_RECEIPT`などを追加する。 |

## レスポンス

読取サービスは次のJSONを返す。値が不明な場合は空文字列にする。

```json
{
  "candidates": {
    "customerName": "",
    "mobilePhone": "",
    "address": "",
    "itemModel1": "",
    "itemModel2": "",
    "requestedDate": "",
    "contactNote": ""
  }
}
```

GPU導入後は、読取サービスを起動してから、外部設定ファイルに次を追加する。共通の `application.properties` は安全のため `false` のままにする。

```properties
order.ai.enabled=true
order.ai.extraction-url=http://127.0.0.1:18080/v1/document-extractions
order.ai.model-name=qwen2.5vl:3b
```

追加先は開発時が `/Users/makoto/config/application-dev.properties`、本番時がUbuntuサーバーの `/etc/kyousei-neo/config/application-prod.properties` とする。既存の「OCR実行」操作でAI読取結果が受注候補画面に表示される。
