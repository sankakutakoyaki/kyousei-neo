package com.kyouseipro.neo.sql.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.service.DispatchScopeLock;
import com.kyouseipro.neo.sql.service.QueryExecutor;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository.DispatchEvent;

class DispatchAssignmentHandlerTest {

    private BaseSqlRepository baseRepository;
    private QueryExecutor queryExecutor;
    private DispatchAssignmentHandler handler;
    private OrderHistoryRepository history;
    private DispatchScopeLock dispatchScopeLock;

    @BeforeEach
    void setUp() {
        baseRepository = mock(BaseSqlRepository.class);
        queryExecutor = mock(QueryExecutor.class);
        history = mock(OrderHistoryRepository.class);

        // 共通ロックはモック化し、単体テストではDB接続しない
        dispatchScopeLock = mock(DispatchScopeLock.class);
        handler = new DispatchAssignmentHandler(
            baseRepository, queryExecutor, history, dispatchScopeLock
        );
    }

    private SelectRequest request(Map<String, Object> params) {
        SelectRequest req = new SelectRequest();
        req.setParams(params);
        return req;
    }

    private QueryDefinition definition(QueryKind kind) {
        QueryDefinition def = mock(QueryDefinition.class);
        when(def.getKind()).thenReturn(kind);
        return def;
    }

    private List<Map<String, Object>> rows(Map<String, Object>... values) {
        return List.of(values);
    }

    private Map<String, Object> assignment(int crewId, int version) {
        return Map.of("dailyCrewId", crewId, "version", version);
    }

    @Test
    void supportsAllFiveOperations() {
        assertTrue(handler.supports(QueryKind.DISPATCH_ASSIGNMENT_CREATE));
        assertTrue(handler.supports(QueryKind.DISPATCH_ASSIGNMENT_MOVE));
        assertTrue(handler.supports(QueryKind.DISPATCH_ASSIGNMENT_REORDER));
        assertTrue(handler.supports(QueryKind.DISPATCH_ASSIGNMENT_REMOVE));
        assertTrue(handler.supports(QueryKind.DISPATCH_ASSIGNMENT_RESET));
    }

    @Test
    void createInsertsAssignment() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            SelectRequest req = invocation.getArgument(1);
            if (req.getParams().containsKey("dailyCrewId") && req.getParams().containsKey("state")) {
                // 班ロック・訪問順序取得を区別
                QueryDefinition query = invocation.getArgument(0);
                if (query.getSql().contains("UPDLOCK")) {
                    return rows(Map.of("dailyCrewId", 8));
                }
                return rows(Map.of("nextVisitOrder", 4));
            }
            return List.of();
        });
        when(baseRepository.insert(eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS), anyMap(), eq("system"))).thenReturn(12L);
        Object result = handler.execute(
            definition(QueryKind.DISPATCH_ASSIGNMENT_CREATE),
            request(Map.of("orderId", 100, "dailyCrewId", 8, "remarks", "テスト"))
        );
        assertEquals(Map.of("data", 12L, "count", 1), result);
        var sequence = inOrder(baseRepository, history);
        sequence.verify(baseRepository).insert(eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS), anyMap(), eq("system"));
        sequence.verify(history).recordDispatch(12L, DispatchEvent.DISPATCH_CREATED, "system");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(baseRepository).insert(eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS), captor.capture(), eq("system"));
        Map<String, Object> inserted = captor.getValue();
        assertEquals(100, inserted.get("orderId"));
        assertEquals(8, inserted.get("dailyCrewId"));
        assertEquals(4, inserted.get("visitOrder"));
        assertEquals("テスト", inserted.get("remarks"));
        assertEquals(0, inserted.get("state"));
    }

    @Test
    void moveKeepsAssignmentIdentity() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {

                QueryDefinition query = invocation.getArgument(0);

                if (query.getSql().contains("UPDLOCK")) {
                    return rows(Map.of("dailyCrewId", 8));
                }

                if (query.getSql().contains(
                    "dispatch_assignment_id = ?"
                )) {
                    return rows(assignment(5, 3));
                }

                return rows(Map.of("nextVisitOrder", 4));
            });

        when(baseRepository.update(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),
            anyMap(),
            eq("system")
        )).thenReturn(1);

        Object result = handler.execute(
            definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE),
            request(Map.of(
                "dispatchAssignmentId", 12,
                "version", 3,
                "dailyCrewId", 8
            ))
        );

        assertEquals(
            Map.of("data", 12, "count", 1),
            result
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor =
            ArgumentCaptor.forClass(Map.class);

        verify(baseRepository).update(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),
            captor.capture(),
            eq("system")
        );

        Map<String, Object> updated = captor.getValue();

        assertEquals(12, updated.get("dispatchAssignmentId"));
        assertEquals(3, updated.get("version"));
        assertEquals(8, updated.get("dailyCrewId"));
        assertEquals(4, updated.get("visitOrder"));

        verify(history).recordDispatch(12L, DispatchEvent.DISPATCH_MOVED, "system");
        assertFalse(updated.containsKey("orderId"));
        assertFalse(updated.containsKey("remarks"));

        verify(baseRepository, never()).insert(
            any(), anyMap(), anyString()
        );
    }

    @Test
    void moveRejectsStaleVersion() {

        when(queryExecutor.select(any(), any()))
            .thenAnswer(invocation -> {

                QueryDefinition query = invocation.getArgument(0);

                if (query.getSql().contains("UPDLOCK")) {
                    return rows(Map.of("dailyCrewId", 8));
                }

                return rows(assignment(5, 4));
            });

        assertThrows(
            BusinessException.class,
            () -> handler.execute(
                definition(QueryKind.DISPATCH_ASSIGNMENT_MOVE),
                request(Map.of(
                    "dispatchAssignmentId", 12,
                    "version", 3,
                    "dailyCrewId", 8
                ))
            )
        );

        verify(baseRepository, never()).update(
            any(), anyMap(), anyString()
        );
        verifyNoInteractions(history);
    }

    @Test
    void reorderRejectsMissingAssignments() {

        when(queryExecutor.select(any(), any()))
            .thenAnswer(invocation -> {

                QueryDefinition query = invocation.getArgument(0);

                if (query.getSql().contains("UPDLOCK")) {
                    return rows(Map.of("dailyCrewId", 8));
                }

                return rows(
                    Map.of("dispatchAssignmentId", 12),
                    Map.of("dispatchAssignmentId", 13)
                );
            });

        List<Map<String, Object>> items = List.of(
            Map.of(
                "dispatchAssignmentId", 12,
                "version", 3,
                "visitOrder", 1
            )
        );

        assertThrows(
            BusinessException.class,
            () -> handler.execute(
                definition(QueryKind.DISPATCH_ASSIGNMENT_REORDER),
                request(Map.of(
                    "dailyCrewId", 8,
                    "items", items
                ))
            )
        );

        verify(baseRepository, never()).update(
            any(), anyMap(), anyString()
        );
        verifyNoInteractions(history);
    }

    @Test
    void removeUsesVersionedLogicalDelete() {

        when(queryExecutor.select(any(), any()))
            .thenAnswer(invocation -> {

                QueryDefinition query = invocation.getArgument(0);

                if (query.getSql().contains("UPDLOCK")) {
                    return rows(Map.of("dailyCrewId", 8));
                }

                return rows(assignment(8, 3));
            });

        when(baseRepository.update(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),
            anyMap(),
            eq("system")
        )).thenReturn(1);

        Object result = handler.execute(
            definition(QueryKind.DISPATCH_ASSIGNMENT_REMOVE),
            request(Map.of(
                "dispatchAssignmentId", 12,
                "version", 3
            ))
        );

        assertEquals(Map.of("count", 1), result);
        verify(history).recordDispatch(12L, DispatchEvent.DISPATCH_REMOVED, "system");

        verify(baseRepository).update(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),
            argThat(row ->
                row.get("dispatchAssignmentId").equals(12)
                && row.get("version").equals(3)
                && row.get("state").equals(9)
                && !row.containsKey("orderId")
            ),
            eq("system")
        );
    }

    @Test
    void resetDeletesOnlySelectedAssignments() {

        when(queryExecutor.select(any(), any()))
            .thenAnswer(invocation -> {

                QueryDefinition query = invocation.getArgument(0);

                if (query.getSql().contains("UPDLOCK")) {
                    return rows(Map.of("dailyCrewId", 8));
                }

                if (query.getSql().contains(
                    "FROM daily_crews"
                )) {
                    return rows(Map.of("dailyCrewId", 8));
                }

                return rows(
                    Map.of(
                        "dispatchAssignmentId", 12,
                        "version", 3
                    )
                );
            });

        when(baseRepository.update(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),
            anyMap(),
            eq("system")
        )).thenReturn(1);

        Object result = handler.execute(
            definition(QueryKind.DISPATCH_ASSIGNMENT_RESET),
            request(Map.of(
                "workDate", "2026-10-12",
                "officeId", 1,
                "dispatchCategory", 1
            ))
        );

        assertEquals(Map.of("count", 1), result);
        verify(history).recordDispatch(12L, DispatchEvent.DISPATCH_RESET, "system");

        verify(baseRepository).update(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS),
            argThat(row ->
                row.get("dispatchAssignmentId").equals(12)
                && row.get("version").equals(3)
                && row.get("state").equals(9)
            ),
            eq("system")
        );

        // 共通ロック → 班一覧取得 → 班ロック → 配車割当取得の順序を確認
        var sequence = inOrder(dispatchScopeLock, queryExecutor);

        sequence.verify(dispatchScopeLock).lock("2026-10-12", 1, 1);

        // 対象班一覧を取得
        sequence.verify(queryExecutor).select(
            argThat(q -> q.getSql().contains("FROM daily_crews")
                && q.getSql().contains("ORDER BY daily_crew_id")),
            any(SelectRequest.class)
        );

        // 対象班をロック
        sequence.verify(queryExecutor).select(
            argThat(q -> q.getSql().contains("UPDLOCK")
                && q.getSql().contains("HOLDLOCK")),
            any(SelectRequest.class)
        );

        // ロック取得後に配車割当を取得
        sequence.verify(queryExecutor).select(
            argThat(q -> q.getSql().contains("FROM dispatch_assignments")),
            any(SelectRequest.class)
        );
    }

    @Test void reorderRecordsEachVersionAfterUpdateAndUsesAuthenticatedEditor() {
        when(queryExecutor.select(any(), any())).thenAnswer(call -> {
            QueryDefinition query = call.getArgument(0);
            if (query.getSql().contains("UPDLOCK")) return rows(Map.of("dailyCrewId", 8));
            return rows(Map.of("dispatchAssignmentId", 12), Map.of("dispatchAssignmentId", 13));
        });
        handler.execute(definition(QueryKind.DISPATCH_ASSIGNMENT_REORDER), request(Map.of(
            "dailyCrewId", 8, "editor", "employee", "items", List.of(
                Map.of("dispatchAssignmentId", 12, "version", 3, "visitOrder", 2),
                Map.of("dispatchAssignmentId", 13, "version", 4, "visitOrder", 1)))));
        var sequence = inOrder(baseRepository, history);
        sequence.verify(baseRepository).update(any(), argThat(p -> p.get("version").equals(3)), eq("employee"));
        sequence.verify(history).recordDispatch(12L, DispatchEvent.DISPATCH_REORDERED, "employee");
        sequence.verify(baseRepository).update(any(), argThat(p -> p.get("version").equals(4)), eq("employee"));
        sequence.verify(history).recordDispatch(13L, DispatchEvent.DISPATCH_REORDERED, "employee");
    }

    @Test void failedAssignmentDoesNotRecordAndHistoryFailureEscapesTransaction() {
        when(queryExecutor.select(any(), any())).thenAnswer(call -> {
            QueryDefinition query = call.getArgument(0);
            if (query.getSql().contains("UPDLOCK")) return rows(Map.of("dailyCrewId", 8));
            return rows(assignment(8, 3));
        });
        when(baseRepository.update(any(), anyMap(), anyString())).thenThrow(new BusinessException("conflict"));
        var req = request(Map.of("dispatchAssignmentId", 12, "version", 3));
        assertThrows(BusinessException.class, () -> handler.execute(definition(QueryKind.DISPATCH_ASSIGNMENT_REMOVE), req));
        verifyNoInteractions(history);
        doReturn(1).when(baseRepository).update(any(), anyMap(), anyString());
        doThrow(new BusinessException("history failed")).when(history).recordDispatch(anyLong(), any(), anyString());
        assertThrows(BusinessException.class, () -> handler.execute(definition(QueryKind.DISPATCH_ASSIGNMENT_REMOVE), req));
    }

    @Test
    void createRejectsDuplicateAssignment() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            QueryDefinition query = invocation.getArgument(0);

            // 対象班のロック
            if (query.getSql().contains("UPDLOCK")) {
                return rows(Map.of("dailyCrewId", 8));
            }

            // 伝票100はすでに班8に登録されている
            if (query.getSql().contains("FROM dispatch_assignments da")) {
                return rows(Map.of("dailyCrewId", 8));
            }

            return List.of();
        });

        // 同じ伝票・同じ班への登録は拒否する
        BusinessException error = assertThrows(BusinessException.class, () ->
            handler.execute(
                definition(QueryKind.DISPATCH_ASSIGNMENT_CREATE),
                request(Map.of("orderId", 100, "dailyCrewId", 8))
            )
        );

        assertEquals(
            "この伝票はすでに同じ班に配車されています。再読み込みしてください。",
            error.getMessage()
        );

        // 重複時にはINSERTも履歴登録も実行しない
        verifyNoInteractions(baseRepository, history);
    }

    @Test
    void createAllowsSameOrderInDifferentCrew() {
        when(queryExecutor.select(any(), any())).thenAnswer(invocation -> {
            QueryDefinition query = invocation.getArgument(0);

            // 登録先の班8をロック
            if (query.getSql().contains("UPDLOCK")) {
                return rows(Map.of("dailyCrewId", 8));
            }

            // 同じ伝票100は、別の班5に登録済み
            if (query.getSql().contains("FROM dispatch_assignments da")) {
                return rows(Map.of("dailyCrewId", 5));
            }

            // 班8の次の訪問順序
            return rows(Map.of("nextVisitOrder", 4));
        });

        when(baseRepository.insert(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS), anyMap(), eq("system")
        )).thenReturn(12L);

        Object result = handler.execute(
            definition(QueryKind.DISPATCH_ASSIGNMENT_CREATE),
            request(Map.of("orderId", 100, "dailyCrewId", 8))
        );

        // 別の班への配車は許可する
        assertEquals(Map.of("data", 12L, "count", 1), result);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        verify(baseRepository).insert(
            eq(Tables.DISPATCH_ASSIGNMENT_BY_IDS), captor.capture(), eq("system")
        );

        assertEquals(100, captor.getValue().get("orderId"));
        assertEquals(8, captor.getValue().get("dailyCrewId"));
        assertEquals(4, captor.getValue().get("visitOrder"));

        verify(history).recordDispatch(12L, DispatchEvent.DISPATCH_CREATED, "system");
    }
}