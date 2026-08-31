package com.kyouseipro.neo.domain.master.item.model;

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