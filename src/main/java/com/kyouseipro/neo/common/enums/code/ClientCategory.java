package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum ClientCategory implements BaseEnum {

    PARTNER(1, "パートナー"),
    SHIPPER(2, "荷主");

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