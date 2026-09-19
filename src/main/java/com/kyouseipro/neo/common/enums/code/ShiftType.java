package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum ShiftType implements BaseEnum {

    NULL(0, "-----"),

    WORK(1, "出勤"),
    HOLIDAY(2, "休み"),
    PAID_LEAVE(3, "有休"),
    AM_LEAVE(4, "午前休"),
    PM_LEAVE(5, "午後休");

    private final int code;
    private final String label;

    ShiftType(int code, String label) {
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