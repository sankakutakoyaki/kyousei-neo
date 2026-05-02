package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum RecycleCategory implements BaseEnum {

    USE(1, "使用日"),
    DELIVERY(2,"引渡日"),
    SHIPPER (3, "発送日"),
    LOSS( 4, "ロス処理日");

    private final int code;
    private final String label;

    RecycleCategory(int code, String label) {
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