package com.kyouseipro.neo.common.enums.code;

import com.kyouseipro.neo.interfaces.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkResultType implements BaseEnum {
    UNREPORTED(0,"未報告",false,false),
    COMPLETED(1,"完了",true,true),
    DELIVERY_FAILED(2,"搬入不可",true,false),
    POSTPONED(3,"日延",true,false),
    ABSENT(4,"不在",true,true);
    private final int code;
    private final String label;
    private final boolean reportable;
    private final boolean confirmationOnly;
    public OrderStatus getOrderStatus(){
        return switch(this){
            case COMPLETED -> OrderStatus.COMPLETED;
            case ABSENT -> OrderStatus.ABSENT;
            case DELIVERY_FAILED,POSTPONED -> OrderStatus.NEEDS_ACTION;
            case UNREPORTED -> OrderStatus.ACTIVE;
        };
    }
}
