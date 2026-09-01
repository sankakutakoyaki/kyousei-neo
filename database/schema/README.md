# 受注PDF取込・OCRのテーブル定義

このフォルダのSQLは SQL Server 用です。いずれも既存テーブル・列がある場合は何もしないため、開発環境と本番環境のどちらでも確認実行できます。

実行順:

1. `order_imports.sql`
2. `20260831_add_order_import_ocr.sql`
3. `20260831_create_order_ocr_layouts.sql`

`order_imports` はPDFの保存先とOCR結果を管理します。実ファイルは `upload.path/order-pdf/` 配下に保存されます。ファイルだけを先に削除しないでください。履歴との対応が失われます。

## 伝票添付ファイル

`20260901_create_attachments.sql` を実行すると、汎用の `attachment_groups` と `attachments` を作成します。
Orderでは `parent_type = 'ORDER'`、`parent_id = order_id` として利用します。実ファイルは
`upload.path/attachments/{attachment_group_id}/` 配下にUUID名で保存されるため、DBレコードを残したまま
ファイルやディレクトリだけを削除しないでください。

## 勤怠打刻

`20260901_rebuild_timeworks.sql` は既存の `timeworks` と `timeworks_edit` を削除し、
1社員・1勤務日につき1件の打刻データを保持する `timeworks` を作成します。
既存の勤怠データが必要な環境では、実行前に必ず退避してください。
