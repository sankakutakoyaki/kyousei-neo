package com.kyouseipro.neo.recycle.price.entity;

import lombok.Data;

@Data
public class RecyclePriceEntity {
    private int recyclePriceId;
    private int recycleMakerId;
    private int recycleItemId;
    private int price;
    private int taxPrice;
    private int version;
    private int state;
}
