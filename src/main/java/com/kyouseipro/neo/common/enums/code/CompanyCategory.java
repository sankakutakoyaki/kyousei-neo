package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum CompanyCategory implements BaseEnum {

    OWN(0, "自社"),
    PARTNER(1, "協力会社");

    private final int code;
    private final String label;

    CompanyCategory(int code, String label) {
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