package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum ClientCategory implements BaseEnum {

    SHIPPER(2, "荷主"),
    SUPPLIER(3, "購買"),
    FACILITY(4, "施設");

    private final int code;
    private final String label;

    ClientCategory(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }
}