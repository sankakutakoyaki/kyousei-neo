package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;

public enum OrderCategory implements BaseEnum {
    VISIT(1, "訪問日", "visit_date"),
    REGIST(2,"登録日", "regist_date");
    

    private final int code;
    private final String label;
    private final String column;

    OrderCategory(int code, String label, String column) {
        this.code = code;
        this.label = label;
        this.column = column;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String getColumn() {
        return column;
    }
}