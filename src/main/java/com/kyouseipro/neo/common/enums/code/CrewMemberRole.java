package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum CrewMemberRole implements BaseEnum {

    MAIN(1, "担当"),
    SUPPORT(2, "助手");

    private final int code;
    private final String label;

    CrewMemberRole(int code, String label) {
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