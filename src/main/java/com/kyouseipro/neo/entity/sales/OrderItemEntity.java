package com.kyouseipro.neo.entity.sales;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderItemEntity {
    private int order_item_id;
    private int order_id;// 受注番号
    private int company_id;// 荷主
    private String company_name;
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
    private String buyer_name;
    private String remarks;// 備考

    private int classification;// 分類：商品・材料・備品・返品
    private String classification_name;
    private int version;
    private int state;
}
