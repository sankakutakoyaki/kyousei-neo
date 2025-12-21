package com.kyouseipro.neo.entity.sales;

import lombok.Data;

@Data
public class WorkPriceEntity {
    private int work_price_id;
    private int work_item_id;
    private int company_id;
    private int price;
    private int version;
    private int state;
}
