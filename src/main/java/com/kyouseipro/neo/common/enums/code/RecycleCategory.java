package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum RecycleCategory implements BaseEnum {
    REGIST(1,"登録日", "regist_date"),
    USE(2, "使用日", "use_date"),
    DELIVERY(3, "引渡日", "delivery_date"),
    SHIPPER(4, "発送日", "shipping_date"),
    LOSS(5, "ロス処理日", "loss_date");

    private final int code;
    private final String label;
    private final String column;

    RecycleCategory(int code, String label, String column) {
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