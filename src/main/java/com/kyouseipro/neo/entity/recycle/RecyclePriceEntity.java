package com.kyouseipro.neo.entity.recycle;

import lombok.Data;

@Data
public class RecyclePriceEntity {
    private int recycle_price_id;
    private int recycle_maker_id;
    private int recycle_item_id;
    private int price;
    private int tax_price;
    private int version;
    private int state;
}
