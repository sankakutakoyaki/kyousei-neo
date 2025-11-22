package com.kyouseipro.neo.entity.sales;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderItemEntity {
    private int order_item_id;
    private int order_id;
    private String item_maker;
    private String item_name;
    private String item_model;
    private int item_quantity;
    private LocalDate arrival_date;
    private int version;
    private int state;
}
