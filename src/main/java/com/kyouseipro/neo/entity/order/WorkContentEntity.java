package com.kyouseipro.neo.entity.order;

import lombok.Data;

@Data
public class WorkContentEntity {
    private int work_content_id;
    private int order_id;
    private String work_content;
    private int work_quantity;
    private int work_payment;
    private int version;
    private int state;
}
