package com.kyouseipro.neo.sql.provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.domain.operations.dispatch.query.DispatchAssignmentSqlProvider;
import com.kyouseipro.neo.sql.controller.QueryController;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;

class DispatchAssignmentSaveTest {
    private SelectRequest move() {
        var request = new SelectRequest();
        request.setQueryId("dispatchAssignmentSave");
        request.setParams(Map.of("dispatchAssignmentId",12,"version",3,"dailyCrewId",8,"visitOrder",4));
        return request;
    }
    private QueryController controller(BaseSqlRepository base) {
        return new QueryController(new SqlProvider(List.of(new DispatchAssignmentSqlProvider())),
            null,null,base,null,new QueryBuilderProvider(List.of()),new QueryHandlerProvider(List.of()));
    }
    @Test void moveUsesOneVersionedUpdateAndKeepsTheAssignmentIdentity() {
        var base = mock(BaseSqlRepository.class);
        when(base.update(eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),anyMap(),eq("system"))).thenReturn(1);
        assertEquals(Map.of("data",12,"count",1),controller(base).request(move()));
        verify(base).update(eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),argThat(row ->
            row.get("dispatchAssignmentId").equals(12) && row.get("version").equals(3)
            && row.get("dailyCrewId").equals(8) && row.get("visitOrder").equals(4)
            && !row.containsKey("orderId") && !row.containsKey("remarks")),eq("system"));
        verifyNoMoreInteractions(base);
    }
    @Test void staleVersionFailureDoesNotFallBackToInsertOrDelete() {
        var base = mock(BaseSqlRepository.class);
        when(base.update(any(),anyMap(),anyString())).thenThrow(new IllegalStateException("stale version"));
        assertThrows(IllegalStateException.class,() -> controller(base).request(move()));
        verify(base).update(eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),anyMap(),eq("system"));
        verifyNoMoreInteractions(base);
    }
    @Test void itemCsvFiltersByTheSelectedItemIds() {
        var definition = com.kyouseipro.neo.sql.query.business.order.OrderItemQuery.orderItemCsv();
        assertTrue(definition.getSql().contains("o.order_item_id IN (:ids)"));
        assertFalse(definition.getSql().contains("o.order_id IN (:ids)"));
    }

}
