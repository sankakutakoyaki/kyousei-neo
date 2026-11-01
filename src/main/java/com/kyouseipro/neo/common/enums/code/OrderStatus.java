package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus implements BaseEnum {
    ACTIVE(0,"未完了"), NEEDS_ACTION(1,"要対応"),
    COMPLETED(2,WorkResultType.COMPLETED.getLabel()),
    ABSENT(3,WorkResultType.ABSENT.getLabel()), CANCELLED(4,"キャンセル");
    private final int code;
    private final String label;
}
