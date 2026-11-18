
package com.kyouseipro.neo.sql.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.service.DispatchScopeLock;
import com.kyouseipro.neo.sql.service.QueryExecutor;

class DailyCrewHandlerTest {

    DispatchScopeLock dispatchScopeLock = mock(DispatchScopeLock.class);

    @Test
    void 一括作成では出勤中の基本乗務員だけを登録する() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);

        // 車両100の担当50・助手70は出勤、車両200の乗務員80は対象外
        List<Map<String, Object>> workingMembers = List.of(
            Map.of("vehicleId", 100, "employeeId", 50, "role", 1, "displayOrder", 1),
            Map.of("vehicleId", 100, "employeeId", 70, "role", 2, "displayOrder", 2),
            Map.of("vehicleId", 200, "employeeId", 80, "role", 1, "displayOrder", 1)
        );

        final boolean[] crewCreated = {false};

        // 出勤者検索 → 既存班なし → 作成後の班ID取得
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> {
                QueryDefinition query = invocation.getArgument(0);
                SelectRequest request = invocation.getArgument(1);
                if(query.getSql().contains("employee_shifts")) return workingMembers;
                if(query.getSql().contains("FROM daily_crews")) {
                    // INSERT前後を判定
                    return crewCreated[0]
                        ? List.of(Map.of("dailyCrewId", 500))
                        : List.of();
                }
                return List.of();
            });

        when(baseRepository.insert(eq(Tables.DAILY_CREW_BY_IDS), anyMap(), anyString()))
            .thenAnswer(invocation -> {
                crewCreated[0] = true;
                return 500L;
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_BULK_CREATE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of(
            "workDate", "2026-11-16",
            "officeId", 1,
            "dispatchCategory", 1,
            "vehicleIds", List.of(100),
            "copyDefaultMembers", true,
            "editor", "test"
        ));

        handler.execute(definition, request);

        // 登録データを取得するCaptorの型を明示
        @SuppressWarnings({"unchecked", "rawtypes"})
        org.mockito.ArgumentCaptor<Map<String, Object>> captor =
            (org.mockito.ArgumentCaptor) org.mockito.ArgumentCaptor.forClass(Map.class);

        verify(baseRepository, times(2)).insert(
            eq(Tables.DAILY_CREW_MEMBER_BY_IDS),
            captor.capture(),
            eq("test")
        );

        List<Map<String, Object>> saved = captor.getAllValues();

        // 出勤者50・70だけが登録され、別車両の80は含まれない
        assertEquals(List.of(50, 70),
            saved.stream().map(row -> row.get("employeeId")).toList());

        // どちらも新しく作成された班500に所属する
        assertTrue(saved.stream().allMatch(row ->
            ((Number) row.get("dailyCrewId")).intValue() == 500));
    }

    @Test
    void 手動追加では基本乗務員をコピーしない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);

        final boolean[] crewCreated = {false};

        // 既存班なし → 班作成後にIDを返す
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> crewCreated[0]
                ? List.of(Map.of("dailyCrewId", 500))
                : List.of());

        when(baseRepository.insert(eq(Tables.DAILY_CREW_BY_IDS), anyMap(), anyString()))
            .thenAnswer(invocation -> {
                crewCreated[0] = true;
                return 500L;
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_BULK_CREATE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of(
            "workDate", "2026-11-16",
            "officeId", 1,
            "dispatchCategory", 1,
            "vehicleIds", List.of(100),
            "copyDefaultMembers", false,
            "editor", "test"
        ));

        handler.execute(definition, request);

        // 班は作成するが、基本乗務員はコピーしない
        verify(baseRepository).insert(
            eq(Tables.DAILY_CREW_BY_IDS), anyMap(), eq("test")
        );
        verify(baseRepository, never()).insert(
            eq(Tables.DAILY_CREW_MEMBER_BY_IDS), anyMap(), anyString()
        );
    }

    @Test
    void 既存の配車班は再作成しない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);

        // すでに班500が登録されている
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> {
                QueryDefinition query = invocation.getArgument(0);
                if(query.getSql().contains("FROM daily_crews")){
                    return List.of(Map.of("dailyCrewId", 500));
                }
                return List.of();
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_BULK_CREATE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of(
            "workDate", "2026-11-16",
            "officeId", 1,
            "dispatchCategory", 1,
            "vehicleIds", List.of(100),
            "copyDefaultMembers", false,
            "editor", "test"
        ));

        Object result = handler.execute(definition, request);

        // 既存班はスキップし、登録・更新しない
        assertEquals(Map.of("count", 0, "memberCount", 0, "skipped", 1), result);
        verifyNoInteractions(baseRepository);
    }

    @Test
    void 登録済み乗務員がいる車両は自動作成しない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);

        // 車両100の基本乗務員は50・70、社員50は既存班に登録済み
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> {
                QueryDefinition query = invocation.getArgument(0);
                String sql = query.getSql();

                if (sql.contains("vehicle_dispatch_categories")) {
                    return List.of(
                        Map.of("vehicleId", 100, "employeeId", 50, "role", 1, "displayOrder", 1),
                        Map.of("vehicleId", 100, "employeeId", 70, "role", 2, "displayOrder", 2)
                    );
                }
                if (sql.contains("SELECT DISTINCT") && sql.contains("daily_crew_members")) {
                    return List.of(Map.of("employeeId", 50));
                }
                return List.of();
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_BULK_CREATE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of(
            "workDate", "2026-11-16",
            "officeId", 1,
            "dispatchCategory", 1,
            "vehicleIds", List.of(100),
            "copyDefaultMembers", true,
            "editor", "test"
        ));

        Object result = handler.execute(definition, request);

        // 重複のため班も乗務員も登録しない
        assertEquals(Map.of("count", 0, "memberCount", 0, "skipped", 1), result);
        verifyNoInteractions(baseRepository);
    }

    @Test
    void 同じ一括処理内で乗務員が重複する車両は作成しない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);
        final boolean[] crewCreated = {false};

        // 車両100と200に、同じ社員50が基本乗務員として設定されている
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> {
                QueryDefinition query = invocation.getArgument(0);
                SelectRequest request = invocation.getArgument(1);
                String sql = query.getSql();

                if (sql.contains("vehicle_dispatch_categories")) {
                    return List.of(
                        Map.of("vehicleId", 100, "employeeId", 50, "role", 1, "displayOrder", 1),
                        Map.of("vehicleId", 200, "employeeId", 50, "role", 1, "displayOrder", 1)
                    );
                }
                if (sql.contains("FROM daily_crews")) {
                    int vehicleId = ((Number) request.getParams().get("vehicleId")).intValue();
                    return vehicleId == 100 && crewCreated[0]
                        ? List.of(Map.of("dailyCrewId", 500))
                        : List.of();
                }
                return List.of();
            });

        when(baseRepository.insert(eq(Tables.DAILY_CREW_BY_IDS), anyMap(), eq("test")))
            .thenAnswer(invocation -> {
                crewCreated[0] = true;
                return 500L;
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_BULK_CREATE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of(
            "workDate", "2026-11-16",
            "officeId", 1,
            "dispatchCategory", 1,
            "vehicleIds", List.of(100, 200),
            "copyDefaultMembers", true,
            "editor", "test"
        ));

        Object result = handler.execute(definition, request);

        // 車両100のみ作成し、社員50を重複登録しない
        assertEquals(Map.of("count", 1, "memberCount", 1, "skipped", 1), result);

        verify(baseRepository, times(1)).insert(
            eq(Tables.DAILY_CREW_BY_IDS), anyMap(), eq("test")
        );
        verify(baseRepository, times(1)).insert(
            eq(Tables.DAILY_CREW_MEMBER_BY_IDS), anyMap(), eq("test")
        );
    }

    @Test
    void 配車済みの班はロック後に削除を拒否する() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);

        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> {
                QueryDefinition query = invocation.getArgument(0);
                String sql = query.getSql();

                // 対象班のロック
                if (sql.contains("UPDLOCK") && sql.contains("HOLDLOCK")) {
                    return List.of(Map.of("dailyCrewId", 8));
                }

                // 配車済み伝票が1件ある
                if (sql.contains("assignment_count") && sql.contains("dispatch_assignments")) {
                    return List.of(Map.of("assignmentCount", 1));
                }

                return List.of();
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_DELETE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of("dailyCrewId", 8, "editor", "test"));

        BusinessException error = assertThrows(BusinessException.class,
            () -> handler.execute(definition, request));

        assertEquals("配車済みの伝票があります。先に配車を解除してください。", error.getMessage());

        // ロック取得 → 配車済み件数確認の順序を検証
        var sequence = inOrder(queryExecutor);
        sequence.verify(queryExecutor).select(
            argThat(query -> query.getSql().contains("UPDLOCK")),
            any(SelectRequest.class)
        );
        sequence.verify(queryExecutor).select(
            argThat(query -> query.getSql().contains("assignment_count")),
            any(SelectRequest.class)
        );

        // 削除が拒否された場合、DBを変更しない
        verifyNoInteractions(baseRepository);
    }

    @Test
    void 再初期化では班をロックしてから配車済みを確認する() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DailyCrewHandler handler = new DailyCrewHandler(baseRepository, queryExecutor, dispatchScopeLock);

        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenAnswer(invocation -> {
                QueryDefinition query = invocation.getArgument(0);
                String sql = query.getSql();

                // 対象班一覧を取得
                if (sql.contains("FROM daily_crews") && sql.contains("ORDER BY daily_crew_id")) {
                    return List.of(Map.of("dailyCrewId", 8));
                }
                // 対象班のロック取得
                if (sql.contains("UPDLOCK") && sql.contains("HOLDLOCK")) {
                    return List.of(Map.of("dailyCrewId", 8));
                }
                // 配車済み伝票が1件存在
                if (sql.contains("assignment_count") && sql.contains("dispatch_assignments")) {
                    return List.of(Map.of("assignmentCount", 1));
                }
                return List.of();
            });

        QueryDefinition definition = mock(QueryDefinition.class);
        when(definition.getKind()).thenReturn(QueryKind.DAILY_CREW_REINITIALIZE);

        SelectRequest request = new SelectRequest();
        request.setParams(Map.of(
            "workDate", "2026-11-18",
            "officeId", 1,
            "dispatchCategory", 1,
            "editor", "test"
        ));

        BusinessException error = assertThrows(BusinessException.class,
            () -> handler.execute(definition, request));

        assertEquals("すでに配車されているため再初期化できません。", error.getMessage());

        // 共通ロック → 対象班取得 → 班ロック → 配車済み確認の順序を検証
        var sequence = inOrder(dispatchScopeLock, queryExecutor);
        sequence.verify(dispatchScopeLock).lock("2026-11-18", 1, 1);
        sequence.verify(queryExecutor).select(
            argThat(q -> q.getSql().contains("ORDER BY daily_crew_id")),
            any(SelectRequest.class)
        );
        sequence.verify(queryExecutor).select(
            argThat(q -> q.getSql().contains("UPDLOCK") && q.getSql().contains("HOLDLOCK")),
            any(SelectRequest.class)
        );
        sequence.verify(queryExecutor).select(
            argThat(q -> q.getSql().contains("assignment_count")),
            any(SelectRequest.class)
        );

        // 配車済みの場合は班・乗務員を変更しない
        verifyNoInteractions(baseRepository);
    }
}
