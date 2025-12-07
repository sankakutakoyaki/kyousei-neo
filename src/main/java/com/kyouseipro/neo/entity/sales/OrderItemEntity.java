package com.kyouseipro.neo.entity.sales;

import java.time.LocalDate;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.interfaceis.CsvExportable;

import lombok.Data;

@Data
public class OrderItemEntity implements CsvExportable {
    private int order_item_id;
    private int order_id;// 受注番号
    private int company_id;// 荷主
    private int office_id;// 支店
    private String company_name;
    private String office_name;
    private String delivery_address;// お届け先

    private LocalDate arrival_date;// 入荷日
    private int inspector_id;// 検品者
    private String inspector_name;
    private int shipping_company_id;// 運送会社
    private String shipping_company_name;
    private String document_number;// 伝票番号

    private String item_maker;// メーカー
    private String item_name;// 商品名
    private String item_model;// 型式
    private int item_quantity;// 数量
    private int item_payment;// 金額
    
    private int buyer_id;// 購入者
    private String buyer_company_name;// 
    private String buyer_name;//
    private String remarks;// 備考

    private int classification;// 分類：商品・材料・備品・返品
    private String classification_name;
    private int version;
    private int state;

    // CSVヘッダーを返す static メソッド（必須ではないですが慣例的に付ける）
    public static String getCsvHeader() {
        return "ID,受注番号,荷主,支店,お届け先,入荷日,検品者,運送会社,伝票番号,メーカー,商品名,型式,数量,金額,購入者,会社名,備考,分類名";
    }

    @Override
    public String toCsvRow() {
        return Utilities.escapeCsv(String.valueOf(order_item_id)) + "," +
               Utilities.escapeCsv(String.valueOf(order_id)) + "," +
               Utilities.escapeCsv(company_name) + "," +
               Utilities.escapeCsv(office_name) + "," +
               Utilities.escapeCsv(delivery_address) + "," +
               Utilities.escapeCsv(String.valueOf(arrival_date)) + "," +
               Utilities.escapeCsv(inspector_name) + "," +
               Utilities.escapeCsv(shipping_company_name) + "," +
               Utilities.escapeCsv(document_number) + "," +
               Utilities.escapeCsv(item_maker) + "," +
               Utilities.escapeCsv(item_name) + "," +
               Utilities.escapeCsv(item_model) + "," +
               Utilities.escapeCsv(String.valueOf(item_quantity)) + "," +
               Utilities.escapeCsv(String.valueOf(item_payment)) + "," +
               Utilities.escapeCsv(buyer_name) + "," +
               Utilities.escapeCsv(buyer_company_name) + "," +
               Utilities.escapeCsv(remarks) + "," +
               Utilities.escapeCsv(classification_name);
    }
}
