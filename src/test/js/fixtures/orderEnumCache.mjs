// APIキャッシュの形を再現するテスト用データ。製品の定義元はJava Enum。
export function installOrderEnumCache(){
    globalThis.APP={cache:{common:{orderEnums:{
        workResult:{UNREPORTED:'未報告',COMPLETED:'完了',DELIVERY_FAILED:'搬入不可',POSTPONED:'日延',ABSENT:'不在'},
        photoCategory:{ESTIMATE:'見積もり',BEFORE:'施工前',COMPLETED:'完了',DELIVERY_FAILED:'搬入不可',OTHER:'その他'},
        orderStatus:{ACTIVE:'未完了',NEEDS_ACTION:'要対応',COMPLETED:'完了',CANCELLED:'キャンセル',ABSENT:'不在'},
        cancelReason:{CUSTOMER:'お客様都合',DELIVERY_FAILED:'搬入不可',SHIPPER:'荷主都合',DUPLICATE:'重複受注',OTHER:'その他'},
        dispatchCategory:{0:'-----',1:'配送',2:'工事'}
    },workResultRules:{UNREPORTED:{confirmationOnly:false},COMPLETED:{confirmationOnly:true},DELIVERY_FAILED:{confirmationOnly:false},POSTPONED:{confirmationOnly:false},ABSENT:{confirmationOnly:true}}}}};
    for(const [key,rule] of Object.entries(globalThis.APP.cache.common.workResultRules))rule.reportable=key!=="UNREPORTED";
}
