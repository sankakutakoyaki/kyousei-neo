# 伝票履歴 第1段階（2026-10-31）

## 調査対象と結論

実リポジトリ `/Users/makoto/Git/kyousei-neo` の HEAD `63e330d204fc10c8f38eba8ee7c697a910bc14f6` と作業中の変更を含むコピーで調査・検証した。プロジェクト同期資料は変更していない。

- JavaのOrderRepositoryはなく、フロントのOrderRepository.jsが/api/queryのorderSaveを呼ぶ。
- QueryControllerはQueryAuthorizationServiceで操作権限を確認し、認証情報からeditorを設定。ORDER_SAVEは専用OrderHandlerへ委譲する。
- OrderHandler.executeの@Transactionalが受注、商品、作業、削除、OCR関連付けの境界。BaseSqlRepositoryのList引数経由の更新・登録はSqlRepositoryのDataSourceUtilsを使い同一接続に参加する。
- SqlBuilderはIDとversionを条件に更新、versionを加算。ログが存在するテーブルはGenericLogSqlProviderがOUTPUT INSERTEDの更新後スナップショットを*_logへ保存する。更新対象ゼロではBaseSqlRepositoryから例外になる。共通SqlBuilderは変更していない。
- 既存history/HistoryAspectは操作の成功・失敗を記録する汎用履歴。伝票ごとの業務履歴とは用途が異なる。
- 配車はDispatchAssignmentHandler.executeの@Transactional。班をUPDLOCK/HOLDLOCKでロックし、移動時は割当versionも再確認する。DailyCrewHandlerも班・担当者更新をトランザクション化しversionを使用。
- 担当伝票MyOrderControllerはログイン従業員と有効な配車・班・班員のEXISTSで一覧・詳細・明細を制限する参照API。今回そこへの履歴表示追加はしていない。
- PDF確定登録はOrderImportRegistrationServiceの別トランザクション経路。今回の履歴対象外。既存DBスクリプトには別の配車テーブル群も残っているが、現行呼出経路に合わせて対象を限定した。
- SqlRepositoryにはDataSource.getConnectionを直接使う別オーバーロード・batchもある。今回の履歴追加は既存のトランザクション参加対応済みList引数経路のみ使用し、共通処理全体の変更はしていない。

## 最小構成と動作

order_historiesは履歴ID、受注ID、保存後version、イベント種別、操作者、日時のみ。既存*_logの住所・訪問日などの値を二重保存しない。order_idとorder_versionからorders_logのスナップショットに対応できる。ただし今回はログの差分取得・表示は提供しない。

orderSaveで本体・明細・OCR処理まで成功した最後に1件追加する。ORDER_CREATEDは新規受注、ORDER_SAVEDは既存受注の保存。変更のない再保存も既存実装がversionを進めるため保存履歴になる。「値が変わった」というイベントとは区別する。明細のみの変更も受注保存として1件。明細単独APIは対象外。

履歴はMANDATORYで既存トランザクションへの参加を必須にし、失敗は握り潰さない。履歴追加失敗時は受注・明細・既存ログもロールバックする。イベント種別のチェック制約と受注ID/versionの一意制約を追加。将来、同一versionで複数種のイベントを記録する場合はこの制約を再検討する。

参照は既存POST /api/queryを利用する。

```json
{"queryId":"orderHistoryList","params":{"orderId":123,"state":0,"beforeHistoryId":null}}
```

受注参照権限orders/selectと、既存OrderAccessServiceのALL/OWN/ASSIGNED/OWN_ASSIGNED条件を適用する。アクセス条件はサーバーが上書き。対象外・権限のない受注は空一覧。操作権限がない場合は既存のアクセス拒否。stateは既存受注詳細APIと同じ呼出規約。

最大100件、新しい履歴ID順。続きは最終行のorderHistoryIdをbeforeHistoryIdに渡す。戻り値は既存と同じdata配列。フロントのOrderRepository.findHistoriesも追加し、省略時のbeforeHistoryIdはnullにする。

## 適用

1. 対象DBのorders.order_idがINT、versionがINTであること、既存orders_logの記録状況を確認する（定義資料と現行コードを前提としたDDL）。
2. database/schema/20261031_create_order_histories.sqlをアプリ更新より先に適用する。既存受注やログの自動移行はしない。既存テーブルがある場合はDDLをスキップするため、列・制約が本定義と一致するか別途確認する。
3. アプリを更新し、検証環境で新規・編集・競合・明細エラー・履歴書込権限不足を確認する。履歴のない古い受注も一覧取得可能。
4. 別営業所・担当外・未認証での参照、101件以上のページング、同時更新時のversionと履歴の一致をSQL Server実機で確認する。

DDL未適用で新アプリを動かすと履歴追加が失敗してorderSave全体がロールバックする。DBの変更・デプロイ・過去ログ移行は今回実行していない。

## 検証結果

Java: 199件、失敗0、エラー0、既存スキップ9（実行190）。JavaScript: 97件すべて成功。Java 23でJava 21向けコンパイル。Mockitoの動的アタッチが実行環境で失敗したため、キャッシュ済みMockito 5.14.2をjavaagentとして指定して再実行した。依存関係の追加・pom変更はしていない。

追加検証は新規ID記録、versionを保持した更新後の記録、更新競合・明細エラー時の記録抑止、履歴失敗の例外伝播、履歴SQL失敗時に同一JDBC接続がrollbackされcommitされないこと、参照条件のクライアント値上書き、参照権限拒否。JDBCはモックで、SQL ServerでのDDL・SQL実行と実データ並行更新は未検証。

再実行例:

```sh
mvn -o test -q -DargLine=-javaagent:/Users/makoto/.m2/repository/org/mockito/mockito-core/5.14.2/mockito-core-5.14.2.jar
node --test src/test/js/*.test.mjs
```

## 次段階

履歴表示UI、住所・訪問日時の差分表示、配車変更、搬入不可・日延・完了・直収、PDF別経路の新規登録、明細単独編集、受注削除、過去ログのバックフィルは未着手。画面実装時はdata-*と共通JS・既存コンポーネントを優先し、CSSはremを使用する。今回HTML/CSS/ページ固有JSは追加していない。
