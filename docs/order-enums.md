# 受注関連のEnum定義と画面への配信

## 定義元

既存の `BaseEnum` に沿って、`common/enums/code` に以下を配置する。

| Enum | 対象 |
| --- | --- |
| WorkResultType | 未報告・完了・搬入不可・日延・不在。報告可能か、確認だけで保存できるかも定義 |
| PhotoCategory | 見積もり・施工前・完了・搬入不可・その他。既存の分類別フォルダ名も定義 |
| OrderStatus | 未完了・要対応・完了・不在・キャンセル |
| OrderCancelReason | キャンセル理由 |
| OrderStatusFilter | 未完了・完了・キャンセル・すべての検索選択肢 |

`WorkResultType.UNREPORTED` は表示用であり、報告APIでは `reportable=false` として拒否する。

## 保存値と互換性

既存のDB・APIの文字列（`COMPLETED`、`ABSENT`、`ESTIMATE` など）はEnum名のまま維持する。数値コードへ一括変換しない。既存の数値Enum用 `EnumUtil.toCombo`／`toMap` は変更せず、文字列の選択肢・表示名に `toNameCombo`／`toNameLabels` を追加した。入力検証は `EnumUtil.byName` でEnumへ解決して行う。

作業結果・写真分類の値、取消理由、フォルダ名は既存データと互換。今回のDB移行は不要。将来、報告可能な結果種別を増やす場合は、DBのチェック制約の移行もあわせて用意する。

## HTML・JS・APIの連携

- HTMLの作業結果ボタン、結果選択肢、写真分類、取消理由はJava Enumの `values()` からThymeleafで生成。画面の構造はHTMLを維持する。
- 受注・配車の既存初期キャッシュへ `common.orderEnums` と `common.workResultRules` を追加。
- 担当伝票は `/api/my-orders/init/cache` を既存の `initPageCache` で読み込み、描画前に定義を取得する。
- JSの表示は `components/order/orderEnums.js` から共通キャッシュを参照する。個別の表示名辞書は持たない。
- 受注一覧、担当カード、未報告件数、配車カード、写真一覧、履歴の値表示に適用。
- キャンセルは作業結果の報告種別とは分け、履歴の結果表示では `OrderStatus.CANCELLED` の表示名を使う。
- 履歴イベントの見出しや確認文など、操作を説明する文章は分類値の表示名とは別に扱う。

## 検証

- Java全体239件：230件成功、既存9件スキップ。
- JS全体137件成功。キャッシュの表示名変更の反映、サーバーの確認ルール、担当一覧のカードと未報告件数を確認。
- Java Enumの実際のキャッシュをJSON化し、JSの状態・写真分類・不在時刻表示・取消履歴まで連携して確認。
- 開発DBで不在・再訪問・完了・日延・キャンセル・写真保存と既存フォルダ名・再送・権限の一連の検証が成功。検証データ・写真はすべて取り消した。
