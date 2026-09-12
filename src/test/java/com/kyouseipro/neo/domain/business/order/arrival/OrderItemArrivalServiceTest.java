package com.kyouseipro.neo.domain.business.order.arrival;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.sql.repository.*;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.common.exception.BusinessException;

class OrderItemArrivalServiceTest {
    final SqlRepository sql = mock(SqlRepository.class);
    final BaseSqlRepository base = mock(BaseSqlRepository.class);
    final OrderItemArrivalService service = new OrderItemArrivalService(sql, base);
    final Map<String,Object> item = Map.of("orderItemId", 10, "itemQuantity", 3, "version", 2);
    Map<String,Object> row(long id, int n, String day) {
        var row = new HashMap<String,Object>(); row.put("arrivalId", id); row.put("quantity", n); row.put("arrivalDate", day); return row;
    }
    void setup(List<Map<String,Object>> rows) {
        when(sql.selectMap(contains("UPDLOCK, HOLDLOCK"), anyList())).thenReturn(List.of(item));
        when(sql.selectMap(contains("ORDER BY arrival_date"), anyList())).thenReturn(rows);
    }
    Map<String,Object> params(int n) {
        return new HashMap<>(Map.of("orderItemId", 10, "version", 2, "quantity", n,
            "arrivalDate", "2026-01-01", "requestId", UUID.randomUUID().toString()));
    }
    @Test void partialReceiptKeepsCompletionDateEmpty() {
        setup(List.of());
        service.record(params(2), "orderItemArrival");
        verify(sql).update(startsWith("INSERT order_item_arrivals"), argThat((List<Object> p) -> p.get(2).equals(2)));
        verify(base).update(eq(Tables.ORDER_ITEM_BY_IDS), argThat(p -> p.containsKey("arrivalDate") && p.get("arrivalDate") == null), anyString());
    }
    @Test void completionUsesLatestActiveDate() {
        setup(List.of(row(1,2,"2026-01-01")));
        when(sql.selectMap(contains("ORDER BY arrival_date"), anyList())).thenReturn(
            List.of(row(1,2,"2026-01-01")), List.of(row(1,2,"2026-01-01"),row(2,1,"2026-01-02")));
        var p=params(1);p.put("arrivalDate","2026-01-02"); service.record(p,"orderItemArrival");
        verify(base).update(any(), argThat(v -> "2026-01-02".equals(String.valueOf(v.get("arrivalDate")))), anyString());
    }
    @Test void rejectsOverReceiptAndStaleScreensBeforeWriting() {
        setup(List.of(row(1,2,"2026-01-01")));
        assertThrows(BusinessException.class, () -> service.record(params(2),"orderItemArrival"));
        var stale=params(1); stale.put("version",1);
        assertThrows(BusinessException.class, () -> service.record(stale,"orderItemArrival"));
        verify(sql, never()).update(anyString(), anyList()); verifyNoInteractions(base);
    }
    @Test void duplicateRequestDoesNotInsertAgainEvenWithOldVersion() {
        setup(List.of());
        when(sql.selectMap(contains("request_id = ?"), anyList())).thenReturn(List.of(Map.of("orderItemId",10)));
        var stale=params(1); stale.put("version",1); service.record(stale,"orderItemArrival");
        verify(sql, never()).update(anyString(), anyList()); verifyNoInteractions(base);
    }
    @Test void correctionCancelsOldRowAndAddsReplacement() {
        setup(List.of(row(1,2,"2026-01-01")));
        var p=params(1);p.put("arrivalId",1);service.record(p,"orderItemArrivalCorrect");
        verify(sql).update(startsWith("UPDATE order_item_arrivals"), anyList());
        verify(sql).update(startsWith("INSERT order_item_arrivals"), argThat((List<Object> v) -> v.get(4).equals(1L)));
    }
    @Test void cancellationRetainsOriginalAndClearsCompletion() {
        setup(List.of(row(1,3,"2026-01-01")));
        var p=params(1);p.put("arrivalId",1); service.record(p,"orderItemArrivalCancel");
        verify(sql).update(startsWith("UPDATE order_item_arrivals"), anyList());
        verify(sql, never()).update(startsWith("INSERT"), anyList());
        verify(base).update(any(), argThat(v -> v.get("arrivalDate") == null), anyString());
    }
    @Test void productQuantityCannotDropBelowReceivedTotal() {
        setup(List.of(row(1,2,"2026-01-01")));
        assertThrows(BusinessException.class, () -> service.save(Map.of("orderItemId",10,"version",2,"itemQuantity",1)));
        verifyNoInteractions(base);
    }
    @Test void productSaveIgnoresClientCompletionDate() {
        setup(List.of());
        service.save(Map.of("orderItemId",10,"version",2,"arrivalDate","2026-01-01","expectedArrivalDate","2026-02-01"));
        verify(base).update(any(), argThat(v -> v.get("arrivalDate") == null && "2026-02-01".equals(v.get("expectedArrivalDate").toString())), anyString());
    }
    @Test void invalidQuantitiesAreRejected() {
        for(Object n:List.of(0,-1,"", "1.5", "2147483648")) assertThrows(BusinessException.class, () -> OrderItemArrivalService.quantity(n));
    }
}
