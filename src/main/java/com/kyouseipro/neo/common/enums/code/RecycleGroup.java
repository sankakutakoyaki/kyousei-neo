package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum RecycleGroup implements BaseEnum {

    A(1, "A"),
    B(2, "B"),
    OTHERS(3, "指定法人");

    private final int code;
    private final String label;

    RecycleGroup(int code, String label) {
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

