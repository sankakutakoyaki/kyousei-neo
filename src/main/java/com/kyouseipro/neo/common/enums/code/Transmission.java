package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum Transmission implements BaseEnum {

    NULL(0, "-----"),
    AT(1, "AT"),
    MT(2, "MT");

    private final int code;
    private final String label;

    Transmission(int code, String label) {
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