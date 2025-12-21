package com.kyouseipro.neo.entity.sales;

import lombok.Data;

@Data
public class WorkItemEntity {
    private int work_item_id;
    private int code;
    private int category;
    private String name;
    private int version;
    private int state;
}
