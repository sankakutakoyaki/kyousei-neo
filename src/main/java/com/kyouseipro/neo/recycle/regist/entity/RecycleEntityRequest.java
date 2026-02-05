package com.kyouseipro.neo.recycle.regist.entity;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class RecycleEntityRequest {
    private List<String> recycleNumbers;// 複数更新用

    private Integer recycleId;
    private String recycleNumber;//　お問合せ管理票番号
    private String moldingNumber;//　成形後の管理票番号
    private Integer makerId;//　メーカー
    private Integer makerCode;
    private String makerName;
    private Integer itemId;//　品目・料金区分
    private Integer itemCode;
    private String itemName;
    private LocalDate useDate;//　使用日
    private LocalDate deliveryDate;//　引渡日
    private LocalDate shippingDate;//　発送日
    private LocalDate lossDate;//　ロス処理日
    private Integer companyId;//　小売業者
    private String companyName;
    private Integer officeId;
    private String officeName;
    private Integer recyclingFee;//　法定リサイクル料
    private Integer disposalSiteId;//　処分場
    private String disposalSiteName;
    private Integer slipNumber;// 伝票番号
    private Integer version;
    private Integer state;
    private LocalDate registDate;// 登録日
    private LocalDate updateDate;// 更新日
}
