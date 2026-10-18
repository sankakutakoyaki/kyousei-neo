package com.kyouseipro.neo.sql.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

class VehicleDefaultMemberHandlerTest {

    @Test
    void 別車両の乗務員は削除できない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        VehicleDefaultMemberHandler handler =
            new VehicleDefaultMemberHandler(baseRepository, sqlRepository);

        // 編集対象は車両100、既存乗務員は車両200
        when(sqlRepository.selectMap(anyString(), anyList()))
            .thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);
                if (sql.contains("vehicle_default_members")) {
                    return List.of(Map.of("vehicleId", 200L));
                }
                return List.of(Map.of(
                    "vehicleDispatchCategoryId", 1L,
                    "dispatchCategory", 1,
                    "version", 1
                ));
            });

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of(
            "vehicleId", 100L,
            "dispatchCategory", 1,
            "editor", "test",
            "deletedMembers", List.of(Map.of(
                "vehicleDefaultMemberId", 10L,
                "version", 1
            ))
        ));

        assertThrows(
            IllegalArgumentException.class,
            () -> handler.execute(null, req)
        );

        verify(baseRepository, never()).update(
            eq(com.kyouseipro.neo.sql.provider.Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS),
            anyMap(),
            anyString()
        );
    }

    @Test
    void 別車両の乗務員は更新できない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        VehicleDefaultMemberHandler handler =
            new VehicleDefaultMemberHandler(baseRepository, sqlRepository);

        when(sqlRepository.selectMap(anyString(), anyList()))
            .thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);
                if (sql.contains("vehicle_default_members")) {
                    return List.of(Map.of("vehicleId", 200L));
                }
                return List.of(Map.of(
                    "vehicleDispatchCategoryId", 1L,
                    "dispatchCategory", 1,
                    "version", 1
                ));
            });

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of(
            "vehicleId", 100L,
            "dispatchCategory", 1,
            "editor", "test",
            "members", List.of(Map.of(
                "vehicleDefaultMemberId", 10L,
                "vehicleId", 200L,
                "employeeId", 50L,
                "role", 1,
                "displayOrder", 1,
                "version", 1
            ))
        ));

        assertThrows(
            IllegalArgumentException.class,
            () -> handler.execute(null, req)
        );

        verify(baseRepository, never()).update(
            eq(Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS),
            anyMap(),
            anyString()
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void 新規登録では編集中の車両IDを使用する() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        VehicleDefaultMemberHandler handler =
            new VehicleDefaultMemberHandler(baseRepository, sqlRepository);

        // 配車区分は登録済み
        when(sqlRepository.selectMap(anyString(), anyList()))
            .thenReturn(List.of(Map.of(
                "vehicleDispatchCategoryId", 1L,
                "dispatchCategory", 1,
                "version", 1
            )));

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of(
            "vehicleId", 100L,
            "dispatchCategory", 1,
            "editor", "test",
            "members", List.of(Map.of(
                "vehicleId", 200L,
                "dispatchCategory", 2,
                "employeeId", 50L,
                "role", 1,
                "displayOrder", 1,
                "state", 0
            ))
        ));

        handler.execute(null, req);

        var rowCaptor = org.mockito.ArgumentCaptor.forClass(Map.class);

        verify(baseRepository).insert(
            eq(Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS),
            rowCaptor.capture(),
            eq("test")
        );

        Map<String, Object> saved = rowCaptor.getValue();

        assertEquals(100L, saved.get("vehicleId"));
        assertEquals(1, saved.get("dispatchCategory"));
        assertEquals(50L, saved.get("employeeId"));
    }
}