package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum DispatchCategory implements BaseEnum {

    NULL(0, "-----"),
    DELIVERY(1, "配送"),
    CONSTRUCTION(2, "工事");

    private final int code;
    private final String label;

    DispatchCategory(int code, String label) {
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