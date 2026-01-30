package com.kyouseipro.neo.sales.order.entity;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaces.CsvExportable;

import lombok.Data;

@Data
public class OrderItemEntity implements CsvExportable {
    private int orderItemId;
    private int orderId;// 受注番号
    private int companyId;// 荷主
    private int officeId;// 支店
    private String companyName;
    private String officeName;
    private String deliveryAddress;// お届け先

    private LocalDate arrivalDate;// 入荷日
    private int inspectorId;// 検品者
    private String inspectorName;
    private int shippingCompanyId;// 運送会社
    private String shippingCompanyName;
    private String documentNumber;// 伝票番号

    private String itemMaker;// メーカー
    private String itemName;// 商品名
    private String itemModel;// 型式
    private int itemQuantity;// 数量
    private int itemPayment;// 金額
    
    private int buyerId;// 購入者
    private String buyerCompanyName;// 
    private String buyerName;//
    private String remarks;// 備考

    private int classification;// 分類：商品・材料・備品・返品
    private String classificationName;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,受注番号,荷主,支店,お届け先,入荷日,検品者,運送会社,伝票番号,メーカー,商品名,型式,数量,金額,購入者,会社名,備考,分類名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(orderItemId)) + "," +
               Utilities.escapeCsv(String.valueOf(orderId)) + "," +
               Utilities.escapeCsv(companyName) + "," +
               Utilities.escapeCsv(officeName) + "," +
               Utilities.escapeCsv(deliveryAddress) + "," +
               Utilities.escapeCsv(String.valueOf(arrivalDate)) + "," +
               Utilities.escapeCsv(inspectorName) + "," +
               Utilities.escapeCsv(shippingCompanyName) + "," +
               Utilities.escapeCsv(documentNumber) + "," +
               Utilities.escapeCsv(itemMaker) + "," +
               Utilities.escapeCsv(itemName) + "," +
               Utilities.escapeCsv(itemModel) + "," +
               Utilities.escapeCsv(String.valueOf(itemQuantity)) + "," +
               Utilities.escapeCsv(String.valueOf(itemPayment)) + "," +
               Utilities.escapeCsv(buyerName) + "," +
               Utilities.escapeCsv(buyerCompanyName) + "," +
               Utilities.escapeCsv(remarks) + "," +
               Utilities.escapeCsv(classificationName);
    }
}
