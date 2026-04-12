package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum State implements BaseEnum {

    INITIAL(0, "標準"),
    UPDATE(1, "更新"),
    COMPLETE(2, "完了"),
    DELETE(9, "削除");

    private final int code;
    private final String label;

    State(int code, String label) {
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
