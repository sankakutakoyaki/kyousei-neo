# 最新版への修正反映（2026-10-12）

土台は `d28223fea6a25cdba89ef6579302d568d26f1111`（おまかせ修正前）。修正版は同じプロジェクト内の `kyousei-neo-latest`。元の `/Users/makoto/Git/kyousei-neo` と、9月版の既存作業フォルダは変更していない。

## 修正済み

- **HTML中心のdispatch表示**: 班・乗務員・割当伝票・伝票カードをcards.htmlのtemplateへ移した。構造、class、ボタン、actionはHTMLで定義し、共通cloneTemplateで複製する。開閉・追加・解除・編集はdata-action経由。ドラッグ操作のみ専用JSに残す。連打防止はdata-action-lock属性とPageControllerの共通処理で指定する。
- **画面のライフサイクル**: 共通PageSessionを追加。controllerRegistryは置換・遷移時にdisposeし、PageController/FormController/DataTableはリスナーと保留表示処理を解放する。loadPageは遷移の競合、読込失敗、CSS失敗を扱い、初期化失敗時は以前の画面を再初期化して復元する。復元時の選択・スクロール状態は保証しない。
- **dispatchの状態と候補取得**: 状態を画面インスタンス内へ移し、古い条件のロードや離脱後の応答を描画に適用しない。乗務員フォームを共通Controllerへ登録し、候補なしのdisabled状態を共通ボタン更新が解除しないようにした。フォームの候補取得も最新のopenだけに適用する。未定義openActiveCrewDialogを呼ぶ未使用アクションと、data-action化後に残ったcallback経路を削除した。検索条件はdispatchConditionsへまとめた。
- **班移動**: 「移動先へINSERT後、元の割当をDELETE」を廃止。同じdispatchAssignmentIdを、version付きの既存共通SAVEで1回UPDATEする。orderId、remarksは更新しないため既存値を保持する。古いversionの更新失敗時に追加・削除へフォールバックしない。ドロップの操作中ガードとエラー表示を追加した。並べ替え失敗時はボードを再取得する。
- **Timeworks**: 既存の分割構成とpersonalMode・モバイル仕様を保持。managementInitialized、社員選択、取得世代、時計・メディアリスナーを画面インスタンス内に閉じた。再入場時に管理一覧のイベントを再設定する。管理検索は最新応答だけを表示し、打刻完了が後から選択した社員をクリアしない。連続打刻もガードする。勤怠編集は入力時の値を保存用スナップショットとし、行ごとに保存を直列化する。離脱時の未送信編集はその値で保存し、離脱後の保存失敗も通知する。古い保存完了が新しい未保存入力を保存済み表示にしない。
- **共通イベント・フォーム**: data-eventの一般指定とaction別指定を保持。Enter二重送信、IME・リピート、disabled、非同期エラーを共通処理で扱う。DataResolverは入力・画面・フォームの世代を確認し、二重初期化と古い検索結果の適用を防ぐ。最新版のAPI resolverとresolvedイベントを保持し、社員入力変更で選択状態を無効化する。フォームのclear/fillは検索状態をリセットする。保存中のフォーム再openと、離脱後の保存完了による別画面のダイアログ閉鎖を防ぐ。
- **以前の共通修正の移植**: createMasterPageの設定引き継ぎとbeforeSaveのawait、edit表記、作業マスターProviderとCSV queryId、マスター2タブの重複ID・再読込、会社/営業所コンボ取得先、商品明細CSVのID条件、日付のローカル日付処理を反映した。未使用で社員SQLのコピーだったWorkSkill定義を削除した。DBのテーブルは変更していない。`.gitignore`のwork指定をルートだけに限定し、Javaソースが無視されないようにした。
- **Java登録の検証**: SQL ProviderとQueryBuilder、Handlerは起動時に重複を検出し、登録元をエラーに出す。最新版の全SQL Provider 16件、QueryBuilderPart 3件、Handler 8件で登録が成立することを確認した。

最新版の所属営業所初期値、own-officeコンボ、モバイル編集制限、Timeworks分割構成を保持している。全面的なbutton fragment化は行っていない。

## 検証結果

- 修正前の最新版: オフラインのpackage成功（テスト省略）。
- 修正後: Javaは43件中38件成功、5件は既存の@Disabledによるスキップ。失敗・エラー0。package成功。
- Mockitoはテスト起動時に5.14.2のjavaagentを明示した。再実行例: `mvn -o -DargLine=-javaagent:$HOME/.m2/repository/org/mockito/mockito-core/5.14.2/mockito-core-5.14.2.jar package`。
- JS: 97件成功、失敗0。dispatch再入場・応答競合、フォームopen競合、保存後の離脱、Timeworks再入場・打刻競合・編集保存、queryId、共通イベントを検証した。
- アプリJS134ファイルの構文確認成功（libを除外）。相対importの存在確認で不足0。差分の空白チェック成功。
- 実際のThymeleafでdispatchとマスターHTMLをレンダリングし、template・共通action・初期値・所属営業所指定とID重複なしを確認した。
- 班移動のフロント送信とJava共通SAVE経路をテストし、単一のversion付きUPDATEと、失敗時にINSERT/DELETEしないことを確認した。

ログはdocs/audit-validation/latest-*に保存した。

## 要確認

実DBへのアクセスと認証済みブラウザー操作は実施していない。アプリ全体の起動テストは既存の@Disabledのままなので、実環境では次を確認する。

1. dispatchを開く→他画面へ移動→再入場し、日付・所属営業所・区分・乗務員編集が正常に動くこと。
2. 日付/営業所を続けて変更し、最後の条件のボード・候補だけが表示されること。
3. 乗務員候補なしの場合に追加ボタンが無効なこと。班を続けて開いた場合に後の班の乗務員だけが表示されること。
4. 班移動後に同じ割当IDのdaily_crew_idだけが変わり、古いversionの競合時に二重配車にならないこと。
5. Timeworksの再入場後も時刻編集・矢印キーが動くこと。社員を続けて選んだ場合に古い応答で上書きされないこと。
6. 勤怠の編集直後に離脱し、編集値が保存されること。本人打刻・所属営業所・モバイルの閲覧制限が維持されること。

複数ブラウザーから同時に割当を追加した場合の重複チェックとvisitOrderの採番は既存方式が残る。今回のversion付き更新で「同じ割当を2つの班へ同時に移動」する競合は失敗にできるが、別割当の同じ伝票や同時採番を完全には制御しない。DBの制約と複数班配車の業務要件を確認してからサーバー側で整える。

## 今後の共通化候補

- Timeworks管理行の動的inputをHTML template化する。
- 機能が増えた場合にドラッグ操作の共通化を検討する。業務ごとの操作まで一律にdata-actionへ押し込めない。
- 元リポジトリにはrecycleMaker/recyclemakerのディレクトリ名違いで、同じ内容の2組がGit登録されている。今回の対象外として保持したが、配布前に参照パスを含めて片方へ統一する。
- DispathAssignmentHandlerの綴りをDispatchAssignmentHandlerへ統一する。

## 適用

プロジェクトのルートにある `latest-html-driven-fixes.patch` は上記HEADからのソース・テスト・報告書の差分。新規ファイルと削除も含む。元リポジトリへ適用する前に、そのリポジトリが修正元HEADのままかを確認し、`git apply --check`で競合がないことを確認する。CSS・DB schemaへの変更はない。以前の9月版作業フォルダのOCR変更を丸ごと含めてはいない。最新版に既にあるOCR実装を保持している。
