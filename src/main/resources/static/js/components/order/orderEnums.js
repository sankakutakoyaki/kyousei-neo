"use strict";

// 表示名と報告ルールは、Java Enumから既存の初期キャッシュで受け取る。
export function orderEnumLabel(group,value,fallback="") {
    if(value===null || value===undefined || value==="")return fallback;
    return globalThis.APP?.cache?.common?.orderEnums?.[group]?.[String(value)] ?? String(value);
}
export const workResultLabel = value => orderEnumLabel("workResult",value);
export const photoCategoryLabel = value => orderEnumLabel("photoCategory",value);
export const orderStatusLabel = value => orderEnumLabel("orderStatus",value);
export const isSimpleWorkResult = value => globalThis.APP?.cache?.common?.workResultRules?.[value]?.confirmationOnly === true;
export const isReportableWorkResult = value => globalThis.APP?.cache?.common?.workResultRules?.[value]?.reportable === true;
export const orderOutcomeLabel = value => value === "CANCELLED" ? orderStatusLabel(value) : workResultLabel(value);
