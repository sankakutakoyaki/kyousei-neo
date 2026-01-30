package com.kyouseipro.neo.sales.order.entity;

import lombok.Data;

@Data
public class WorkContentEntity {
    private int workContentId;
    private int orderId;
    private String workContent;
    private int workQuantity;
    private int workPayment;
    private int version;
    private int state;
}
