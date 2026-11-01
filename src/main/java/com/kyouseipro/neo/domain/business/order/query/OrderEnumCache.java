package com.kyouseipro.neo.domain.business.order.query;

import java.util.*;
import java.util.stream.Collectors;
import com.kyouseipro.neo.common.enums.code.*;
import com.kyouseipro.neo.common.enums.util.EnumUtil;

public final class OrderEnumCache {
    private OrderEnumCache() {}
    public static Map<String,Object> common(){
        var labels=Map.of("workResult",EnumUtil.toNameLabels(WorkResultType.class),
            "photoCategory",EnumUtil.toNameLabels(PhotoCategory.class),"orderStatus",EnumUtil.toNameLabels(OrderStatus.class),
            "cancelReason",EnumUtil.toNameLabels(OrderCancelReason.class),"dispatchCategory",EnumUtil.toCodeLabels(DispatchCategory.class));
        var rules=Arrays.stream(WorkResultType.values()).collect(Collectors.toMap(Enum::name,
            type->Map.of("reportable",type.isReportable(),"confirmationOnly",type.isConfirmationOnly())));
        return Map.of("orderEnums",labels,"workResultRules",rules);
    }
    public static Map<String,Object> page(){
        return Map.of("orderStatusComboList",EnumUtil.toNameCombo(OrderStatusFilter.class));
    }
}
