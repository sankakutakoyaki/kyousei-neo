# 受注PDF取込・OCRのテーブル定義

このフォルダのSQLは SQL Server 用です。いずれも既存テーブル・列がある場合は何もしないため、開発環境と本番環境のどちらでも確認実行できます。

実行順:

1. `order_imports.sql`
2. `20260831_add_order_import_ocr.sql`
3. `20260831_create_order_ocr_layouts.sql`
4. `20260905_create_ai_document_reviews.sql`

`order_imports` はPDFの保存先とOCR結果を管理します。実ファイルは `upload.path/order-pdf/` 配下に保存されます。ファイルだけを先に削除しないでください。履歴との対応が失われます。

## 伝票添付ファイル

`20260901_create_attachments.sql` を実行すると、汎用の `attachment_groups` と `attachments` を作成します。
Orderでは `parent_type = 'ORDER'`、`parent_id = order_id` として利用します。実ファイルは
`upload.path/attachments/{attachment_group_id}/` 配下にUUID名で保存されるため、DBレコードを残したまま
ファイルやディレクトリだけを削除しないでください。

## AI読取の確認・学習用データ

`20260905_create_ai_document_reviews.sql` を実行すると、AIの元回答と人が確認して確定した値を、業務データとは分けて保存する `ai_document_reviews` を作成します。

現在は受注FAXを `document_type = 'ORDER_FAX'` として保存します。将来のレシートは `EXPENSE_RECEIPT`、写真は `PHOTO` のように追加できます。追加学習や精度評価には、`review_status = 'CONFIRMED'` のデータだけを使用します。

## 勤怠打刻

`20260901_rebuild_timeworks.sql` は既存の `timeworks` と `timeworks_edit` を削除し、
1社員・1勤務日につき1件の打刻データを保持する `timeworks` を作成します。
既存の勤怠データが必要な環境では、実行前に必ず退避してください。

既に `timeworks` を作成済みの環境では、原本を削除せずに
`20260905_create_timework_edits.sql` のみを実行してください。修正値は履歴テーブルへ保存され、
`timeworks.start_time` と `timeworks.end_time` は変更されません。

## 商品入荷履歴

`20260912_create_order_item_arrivals.sql` は入荷履歴と入荷予定日の列を追加し、
既存の入荷済み商品を初期履歴へ移行します。新版アプリの起動前に、書込みを停止した状態で適用してください。
詳細は [入荷履歴の手順](../../docs/order-item-arrivals.md) を参照してください。

## 配車

1. `20260913_create_order_dispatch.sql` で配車本体を作成。
2. `20260913_add_order_dispatch_log.sql` で変更ログを追加。

本体を適用済みの場合は2だけ実行してください。両方ともアプリ停止・バックアップ後に適用します。ログ追加前のデータはMIGRATEとして初期記録を残します。詳細は [配車の手順](../../docs/order-dispatch.md) を参照してください。

## 運行・資格・労務管理

旧配車の2本と添付テーブルの適用後、`20260913_create_operations.sql` を適用してください。
車両・日別編成・複数台配車・点検・手入力得点・資格・労務・健康診断と、それぞれの変更ログを追加します。
旧配車データは保存し、車両への自動変換はしません。
実行前のバックアップと停止、権限と動作確認は [運行管理の手順](../../docs/operations.md) を参照してください。

## 受注の担当営業所

`20260915_add_order_own_office.sql` は受注本体とログへ自社担当営業所を追加します。
新しい版の起動前に、アプリ停止・バックアップ後に適用してください。
既存受注は未設定のまま保持します。荷主の支店情報には影響しません。
