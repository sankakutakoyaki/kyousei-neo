package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatusFilter implements BaseEnum {
    INCOMPLETE(1,OrderStatus.ACTIVE.getLabel()), COMPLETED(2,OrderStatus.COMPLETED.getLabel()),
    CANCELLED(3,OrderStatus.CANCELLED.getLabel()), ALL(4,"すべて");
    private final int code;
    private final String label;
}
