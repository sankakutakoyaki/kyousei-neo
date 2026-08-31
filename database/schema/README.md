# 受注PDF取込・OCRのテーブル定義

このフォルダのSQLは SQL Server 用です。いずれも既存テーブル・列がある場合は何もしないため、開発環境と本番環境のどちらでも確認実行できます。

実行順:

1. `order_imports.sql`
2. `20260831_add_order_import_ocr.sql`
3. `20260831_create_order_ocr_layouts.sql`

`order_imports` はPDFの保存先とOCR結果を管理します。実ファイルは `upload.path/order-pdf/` 配下に保存されます。ファイルだけを先に削除しないでください。履歴との対応が失われます。
