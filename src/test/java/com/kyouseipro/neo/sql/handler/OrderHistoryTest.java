package com.kyouseipro.neo.sql.handler;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.common.enums.system.*;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
import com.kyouseipro.neo.domain.business.order.arrival.OrderItemArrivalService;
import com.kyouseipro.neo.domain.business.order.ocr.OrderOcrAttachmentService;
import com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLogRepository;
import com.kyouseipro.neo.sql.model.*;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.*;

class OrderHistoryTest {
    private final BaseSqlRepository base = mock(BaseSqlRepository.class);
    private final OrderHistoryRepository history = mock(OrderHistoryRepository.class);
    private final OrderHandler handler = new OrderHandler(base, history,
        mock(OrderItemArrivalService.class), mock(SqlRepository.class),
        mock(OrderOcrLogRepository.class), mock(OrderOcrAttachmentService.class));
    private final QueryDefinition definition = new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_BY_IDS);
    private SelectRequest request(boolean created) {
        var req = new SelectRequest();
        var params = new LinkedHashMap<String, Object>();
        params.put("editor", "employee");
        if (!created) { params.put("orderId", 12L); params.put("version", 3); }
        req.setParams(params);
        return req;
    }
    @Test void newOrderRecordsGeneratedIdAfterSaving() {
        when(base.insert(any(), anyMap(), anyString())).thenReturn(12L);
        handler.execute(definition, request(true));
        var order = inOrder(base, history);
        order.verify(base).insert(any(), anyMap(), eq("employee"));
        order.verify(history).recordSaved(12L, true, "employee");
    }
    @Test void editRecordsSaveAfterVersionCheckedUpdate() {
        handler.execute(definition, request(false));
        var order = inOrder(base, history);
        order.verify(base).update(any(), argThat(p -> p.get("version").equals(3)), eq("employee"));
        order.verify(history).recordSaved(12L, false, "employee");
    }
    @Test void conflictDoesNotRecordHistory() {
        when(base.update(any(), anyMap(), anyString())).thenThrow(new BusinessException("conflict"));
        assertThrows(BusinessException.class, () -> handler.execute(definition, request(false)));
        verifyNoInteractions(history);
    }
    @Test void detailFailureDoesNotRecordHistory() {
        var req = request(false);
        req.getParams().put("works", List.of(Map.of("orderWorkId", 0)));
        when(base.insert(eq(Tables.ORDER_WORK_BY_IDS), anyMap(), anyString())).thenThrow(new BusinessException("detail failed"));
        assertThrows(BusinessException.class, () -> handler.execute(definition, req));
        verifyNoInteractions(history);
    }
    @Test void historyFailureIsPropagatedToTransaction() {
        doThrow(new BusinessException("history failed")).when(history).recordSaved(anyLong(), anyBoolean(), anyString());
        assertThrows(BusinessException.class, () -> handler.execute(definition, request(false)));
    }
}
