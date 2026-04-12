package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum Gender implements BaseEnum {

    NULL(0, ""),
    MAN(1, "男性"),
    WOMAN(2, "女性"),
    OTHERS(9, "無回答");

    private final int code;
    private final String label;

    Gender(int code, String label) {
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
