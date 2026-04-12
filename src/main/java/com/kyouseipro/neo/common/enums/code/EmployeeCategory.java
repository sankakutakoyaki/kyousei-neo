package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum EmployeeCategory implements BaseEnum {

    FULLTIME(1, "社員"),
    PARTTIME(2, "アルバイト"),
    CONSTRUCT(3, "請負");

    private final int code;
    private final String label;

    EmployeeCategory(int code, String label) {
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
