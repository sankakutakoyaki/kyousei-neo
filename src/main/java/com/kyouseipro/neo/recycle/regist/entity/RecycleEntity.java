package com.kyouseipro.neo.recycle.regist.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class RecycleEntity implements CsvExportable {
    private int recycleId;
    private String recycleNumber;//　お問合せ管理票番号
    private String moldingNumber;//　成形後の管理票番号
    private int makerId;//　メーカー
    private int makerCode;
    private String makerName;
    private int itemId;//　品目・料金区分
    private int itemCode;
    private String itemName;
    private LocalDate useDate;//　使用日
    private LocalDate deliveryDate;//　引渡日
    private LocalDate shippingDate;//　発送日
    private LocalDate lossDate;//　ロス処理日
    private int companyId;//　小売業者
    private String companyName;
    private int officeId;
    private String officeName;
    private int recyclingFee;//　法定リサイクル料
    private int disposalSiteId;//　処分場
    private String disposalSiteName;
    private int slipNumber;
    private int version;
    private int state;
    private LocalDate registDate;// 登録日
    private LocalDate updateDate;// 更新日

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,お問合せ管理票番号,コード,製造業者等名,コード,品目・料金区分,使用日,引渡日,発送日,ロス処理日,小売業者,支店,リサイクル料,処分場,登録日,最終更新日";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(Objects.toString(recycleId, "")) + "," +
               Utilities.escapeCsv(moldingNumber) + "," +
               Utilities.escapeCsv(Objects.toString(makerCode, "")) + "," +
               Utilities.escapeCsv(makerName) + "," +
               Utilities.escapeCsv(Objects.toString(itemCode, "")) + "," +
               Utilities.escapeCsv(itemName) + "," + 
               Utilities.escapeCsv(Objects.toString(useDate, "")) + "," +
               Utilities.escapeCsv(Objects.toString(deliveryDate, "")) + "," +
               Utilities.escapeCsv(Objects.toString(shippingDate, "")) + "," +
               Utilities.escapeCsv(Objects.toString(lossDate, "")) + "," +
               Utilities.escapeCsv(companyName) + "," +
               Utilities.escapeCsv(officeName) + "," +
               Utilities.escapeCsv(Objects.toString(recyclingFee, "")) + "," +
               Utilities.escapeCsv(Objects.toString(disposalSiteName, "")) + "," +
               Utilities.escapeCsv(Objects.toString(registDate, "")) + "," +
               Utilities.escapeCsv(Objects.toString(updateDate, ""));
    }
}
