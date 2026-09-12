# 受注OCRの共通フォーム化（2026-09-12）

## 変更内容

受注一覧の荷主を選び、一覧テーブル領域へPDFをドロップする。ファイル選択も利用可能。複数PDFは先頭ページを1件ずつ読み取り、既存の新規・修正フォームで確認する。保存またはキャンセルすると次へ進む。取込結果の一覧は画面に残さない。失敗はファイル名・理由をダイアログで通知し、閉じると次のPDFへ進む。キャンセルした候補は受注登録されない。原本とログはDB・保存フォルダに残る。

受注の「取込」タブ、OCR専用候補フォーム、未使用の帳票枠編集UIを撤去した。受注一覧の「商品」タブは業務メニューの `/products` に移した。商品検索、入荷、JAN検索、商品登録は既存処理を再利用する。個々の受注フォーム内の商品・作業明細タブは維持する。

## 保存経路と影響範囲

1. `OrderPdfImportController` → `OrderPdfImportService.save`: 既存の原本保存と `order_imports` を継続する。
2. FastAPI `/v1/document-extractions` → `OrderAiExtractionClient`: candidates / modelName / promptVersion を受け取る。Javaの設定値ではなく実際の応答メタデータを記録する。
3. `OrderOcrLogRepository`: 作成済みの `dbo.order_ocr_logs` に、order_id=NULLで初回candidates JSONをINSERTする。同一order_import_idの再試行は初回ログを再利用し、再抽出やJSONの上書きをしない。原本管理行のロックで同一原本の同時処理を直列化する。
4. `orderPdfImport.js` → `mapOcrCandidate.js` → `FormController` / `createOrderPage.js`: 共通フォームに初期値とocrLogIdを渡す。候補の複製を編集し、AIログは変更しない。OCR時は一覧の検索条件を初期値へ再コピーせず、ドロップ時に選択した荷主を維持する。郵便番号が未変更の空欄なら住所を消さないようDataResolverも修正した。
5. 既存 `/api/query` の `orderSave` → `OrderHandler`: 新規受注・商品・作業明細を保存し、発行されたorder_idをログに紐付ける。同じSpringトランザクション内でログをロックし、登録済み・別荷主のログは拒否する。紐付け失敗は受注保存もロールバックする。

`SqlRepository.update()` は独立した接続を取得する既存実装のため、ログの紐付けにはトランザクション接続を使う `queryOne` と `UPDATE ... OUTPUT` を利用した。共通Repository全体の挙動は変更していない。

旧 `/api/order/import/{id}/candidate` は廃止。旧登録Service/Repository/モデルは履歴参照用に残っているが、新しい画面・Controllerからは呼ばない。OCRログに代わるAI学習データの二重保存も行わない。OCRの返却形式は `{candidates, ocrLogId, modelName, promptVersion}` に変更したので、Spring側と画面側は同時に反映する。

## データの対応

| OCR | orders |
| --- | --- |
| customerName | title |
| requestedDate | visit_date |
| mobilePhone | contact_information |
| postalCode | postal_code |
| address | full_address |
| contactNote | remarks |
| ドロップ時の荷主 | prime_constractor_id |

年不明・不正な日付は自動補完せず、読取値を共通フォームに表示して確認を求める。商品・作業は既存の明細リストへ反映する。商品・作業の修正は既存の削除・入力・登録操作を利用する。OCR由来の文字列を一覧へ表示するときはHTMLをエスケープする。

## FastAPI

Ollamaの既定モデル `qwen2.5vl:3b`、PDF 200dpi、先頭ページのみの読取を維持。荷主1085では requestedDate を「帳票下部の連絡事項欄の直前に記載されている日付（据付工事御希望日）」と指定する。プロンプト識別子は `heiwado-20260912-01`、共通は `common-20260912-01`。DB接続処理は追加していない。

この受注フローは `order.ai.enabled=true` が必要。AI無効時に別方式で誤った候補を返さないよう、明示的なエラーにする。既存の帳票プレビューAPIは維持する。

## 確認状況

- Java: DB不要の関連テスト15件成功。新規ログ、再試行、共通保存、ログIDのordersへの混入防止、トランザクション接続・ロールバックを確認。
- JavaScript: 16件成功。PDFキュー、共通フォーム呼び出し、キャンセル、日付不明、既存スマホ一覧の回帰テストを含む。
- Python: 構文確認済み。FastAPI本体とOllamaはこの作業環境にはなく、実帳票の読取は未検証。
- 全体のSpring起動テストはDB接続設定不足で失敗。実SQL Server・ログイン済みブラウザでの結合確認、画面の目視確認、本番への反映は未実施。

## 反映前に行う結合確認

作成済みorder_ocr_logsが提示スキーマどおりであること、既存order_importsと原本保存ディレクトリが利用可能であることを確認する。平和堂のPDFで、初回ログのorder_idがNULLになること、フォーム修正後もai_result_jsonが変わらず受注IDだけ紐付くこと、登録済みログを再利用すると新規受注が増えないことを確認する。手入力の新規・既存修正、商品の検索・入荷・JAN検索も確認する。

作業コピーで検証したOCR変更を、2026-09-12に `/Users/makoto/Git/kyousei-neo` へ反映した。反映直前の未コミット変更との競合がないことを確認し、既存の編集内容は保持した。Ubuntu・本番環境には反映していない。

## 追加変更

受注・商品を「一覧」だけのタブ構成にした。PDFボタンは受注の検索ボタン右隣に配置した。

OCR受注を新規保存すると「添付」の「取込PDF」フォルダへ原本のコピーを自動登録する。添付登録も受注・ログ紐付けと同じトランザクションに参加し、失敗時はDB変更と添付用コピーを取り消す。OCR原本は保持する。既に保存済みの受注への一括追加は行わない。

商品一覧は未入荷、受注未設定、配送工事未完了のいずれかに該当するものを表示する。完了判定はcomplete_dateの登録、またはstate=2。完了済み受注もJOIN対象に含め、受注未設定と誤判定しない。入荷状態などの既存検索条件はこの対象範囲内で適用する。

追加変更後、Javaの関連テスト16件・JavaScript16件に成功。添付用コピーの作成とロールバック時の削除、原本の保持を検証した。実DBとブラウザでの結合確認は未実施。
