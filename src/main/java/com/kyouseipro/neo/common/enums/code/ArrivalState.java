package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum ArrivalState implements BaseEnum {

    NOT(1, "未入荷"),
    COMPLETE(2, "入荷済み");
    

    private final int code;
    private final String label;

    ArrivalState(int code, String label) {
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
