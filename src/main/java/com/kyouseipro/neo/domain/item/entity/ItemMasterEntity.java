package com.kyouseipro.neo.domain.item.entity;

import lombok.Data;

@Data
public class ItemMasterEntity {

    private long itemMasterId;
    private String janCode;
    private String itemMaker;
    private String itemName;
    private String itemModel;
    private int state;
    private int version;
}