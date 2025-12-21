package com.kyouseipro.neo.entity.regist;

import lombok.Data;

@Data
public class WorkItemEntity {
    private int work_item_id;
    private int code;
    private int category_id;
    private String category_name;
    private String name;
    private int version;
    private int state;
}
