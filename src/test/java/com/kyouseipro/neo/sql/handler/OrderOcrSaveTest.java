package com.kyouseipro.neo.sql.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.sql.repository.*;
import com.kyouseipro.neo.sql.model.*;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLogRepository;
import com.kyouseipro.neo.common.enums.system.*;

class OrderOcrSaveTest {
    @Test
    void sharedOrderSaveLinksGeneratedIdAndDoesNotSendLogIdToOrders() {
        var base = mock(BaseSqlRepository.class);
        var logs = mock(OrderOcrLogRepository.class);
        when(base.insert(eq(Tables.ORDER_BY_IDS), anyMap(), anyString())).thenReturn(1001L);
        var handler = new OrderHandler(base, mock(SqlRepository.class), logs, mock(com.kyouseipro.neo.domain.business.order.ocr.OrderOcrAttachmentService.class));
        var req = new SelectRequest();
        req.setParams(new LinkedHashMap<>(Map.of("ocrLogId", 7, "primeConstractorId", 1085, "title", "修正後")));
        var def = new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_BY_IDS);
        var result = handler.execute(def, req);
        assertEquals(Map.of("data", 1001L, "count", 1), result);
        verify(logs).link(7, 1001, 1085);
        verify(base).insert(eq(Tables.ORDER_BY_IDS), argThat(p -> !p.containsKey("ocrLogId") && "修正後".equals(p.get("title"))), anyString());
    }

    @Test
    void failedLinkEscapesTransactionalHandlerRatherThanReportingSaved() {
        var base = mock(BaseSqlRepository.class);
        var logs = mock(OrderOcrLogRepository.class);
        when(base.insert(eq(Tables.ORDER_BY_IDS), anyMap(), anyString())).thenReturn(1001L);
        doThrow(new IllegalStateException("already linked")).when(logs).link(7, 1001, 1085);
        var handler = new OrderHandler(base, mock(SqlRepository.class), logs, mock(com.kyouseipro.neo.domain.business.order.ocr.OrderOcrAttachmentService.class));
        var req = new SelectRequest();
        req.setParams(new LinkedHashMap<>(Map.of("ocrLogId", 7, "primeConstractorId", 1085)));
        assertThrows(IllegalStateException.class, () -> handler.execute(new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_BY_IDS), req));
    }
    @Test
    void unknownOcrQuantityIsRejectedBeforeAnyInsert() {
        var base = mock(BaseSqlRepository.class);
        var handler = new OrderHandler(base, mock(SqlRepository.class), mock(OrderOcrLogRepository.class), mock(com.kyouseipro.neo.domain.business.order.ocr.OrderOcrAttachmentService.class));
        var req = new SelectRequest();
        req.setParams(new LinkedHashMap<>(Map.of("ocrLogId", 7, "primeConstractorId", 1085,
            "items", List.of(Map.of("itemModel", "A", "itemQuantity", "")))));
        assertThrows(com.kyouseipro.neo.common.exception.BusinessException.class,
            () -> handler.execute(new QueryDefinition(QueryType.UPDATE, QueryKind.ORDER_SAVE, Tables.ORDER_BY_IDS), req));
        verifyNoInteractions(base);
    }
}
