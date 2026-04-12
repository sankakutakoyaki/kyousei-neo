package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum BloodType implements BaseEnum {

    NULL(0, ""),
    A(1, "A型"),
    B(2, "B型"),
    O(3, "O型"),
    AB(4, "AB型"),
    OTHERS(9, "無回答");

    private final int code;
    private final String label;

    BloodType(int code, String label) {
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
