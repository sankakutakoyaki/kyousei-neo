package com.kyouseipro.neo.common.enums;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.common.enums.code.*;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.business.order.query.OrderEnumCache;

class OrderEnumCacheTest {
    @Test void persistedNamesAndPhotoFolderNamesStayCompatible(){
        assertEquals("COMPLETED",WorkResultType.COMPLETED.name());assertEquals("完了",WorkResultType.COMPLETED.getLabel());
        assertEquals("見積もり写真",PhotoCategory.ESTIMATE.getFolderName());assertEquals("搬入不可写真",PhotoCategory.DELIVERY_FAILED.getFolderName());
        assertFalse(WorkResultType.UNREPORTED.isReportable());assertTrue(WorkResultType.ABSENT.isConfirmationOnly());
        assertNull(EnumUtil.byName(WorkResultType.class,"OTHER"));assertNull(EnumUtil.byName(PhotoCategory.class,null));
    }
    @Test void cacheLabelsAndChoicesComeFromEnums(){
        var common=OrderEnumCache.common();var labels=(Map<?,?>)common.get("orderEnums");
        assertEquals(EnumUtil.toNameLabels(WorkResultType.class),labels.get("workResult"));
        assertEquals(EnumUtil.toNameLabels(PhotoCategory.class),labels.get("photoCategory"));
        assertEquals(List.of("INCOMPLETE","COMPLETED","CANCELLED","ALL"),EnumUtil.toNameCombo(OrderStatusFilter.class).stream().map(row->row.get("value")).toList());
        assertEquals("その他",OrderCancelReason.OTHER.getLabel());
    }
}
