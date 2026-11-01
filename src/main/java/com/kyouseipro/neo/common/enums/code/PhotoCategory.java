package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhotoCategory implements BaseEnum {
    ESTIMATE(1,"見積もり"), BEFORE(2,"施工前"),
    COMPLETED(3,WorkResultType.COMPLETED.getLabel()),
    DELIVERY_FAILED(4,WorkResultType.DELIVERY_FAILED.getLabel()), OTHER(5,"その他");
    private final int code;
    private final String label;
    public String getFolderName(){return label+"写真";}
}
