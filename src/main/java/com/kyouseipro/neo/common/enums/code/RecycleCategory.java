package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.BaseEnum;

public enum RecycleCategory implements BaseEnum {
    USE(1, "使用日", "use_date"),
    DELIVERY(2, "引渡日", "delivery_date"),
    SHIPPER(3, "発送日", "shipping_date"),
    LOSS(4, "ロス処理日", "loss_date");

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