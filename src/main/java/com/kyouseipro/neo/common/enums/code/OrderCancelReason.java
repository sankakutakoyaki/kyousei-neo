package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderCancelReason implements BaseEnum {
    CUSTOMER(1,"お客様都合"), DELIVERY_FAILED(2,WorkResultType.DELIVERY_FAILED.getLabel()),
    SHIPPER(3,"荷主都合"), DUPLICATE(4,"重複受注"), OTHER(5,PhotoCategory.OTHER.getLabel());
    private final int code;
    private final String label;
}
