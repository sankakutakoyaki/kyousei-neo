package com.kyouseipro.neo.domain.operations.vehicle.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

class VehicleDefaultMemberServiceTest {
    
    @Test
    void 別車両の乗務員は削除できない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        VehicleDefaultMemberService service =
            new VehicleDefaultMemberService(baseRepository, sqlRepository);

        // 操作対象は車両100、乗務員の所属先は車両200
        when(sqlRepository.selectMap(anyString(), anyList()))
            .thenReturn(List.of(Map.of("vehicleId", 200L)));

        Map<String, Object> params = Map.of(
            "vehicleId", 100L,
            "deletedMembers", List.of(Map.of(
                "vehicleDefaultMemberId", 10L,
                "version", 1
            ))
        );

        // 別車両の乗務員は削除できない
        assertThrows(IllegalArgumentException.class,
            () -> service.deleteMembers(params, "test"));

        // DBの更新処理が実行されていないことを確認
        verify(baseRepository, never()).update(
            eq(Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS),
            anyMap(),
            anyString()
        );
    }

    @Test
    void 別車両の乗務員は更新できない() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        VehicleDefaultMemberService service =
            new VehicleDefaultMemberService(baseRepository, sqlRepository);

        // 乗務員10は車両200に所属している
        when(sqlRepository.selectMap(anyString(), anyList()))
            .thenAnswer(invocation -> {
                String sql = invocation.getArgument(0);
                if(sql.contains("vehicle_default_members")){
                    return List.of(Map.of(
                        "vehicleId", 200L,
                        "role", 2,
                        "displayOrder", 1
                    ));
                }
                return List.of();
            });

        Map<String, Object> params = Map.of(
            "vehicleId", 100L,
            "dispatchCategory", 1,
            "members", List.of(Map.of(
                "vehicleDefaultMemberId", 10L,
                "employeeId", 50L,
                "role", 1,
                "displayOrder", 1,
                "version", 1
            ))
        );

        // 別車両の乗務員は更新できず、DB更新も実行されない
        assertThrows(IllegalArgumentException.class,
            () -> service.saveMembers(params, "test"));

        verify(baseRepository, never()).update(
            eq(Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS),
            anyMap(),
            anyString()
        );
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void 新規登録では編集中の車両IDを使用する() {
        BaseSqlRepository baseRepository = mock(BaseSqlRepository.class);
        SqlRepository sqlRepository = mock(SqlRepository.class);
        VehicleDefaultMemberService service =
            new VehicleDefaultMemberService(baseRepository, sqlRepository);

        // 編集対象は車両100・配車区分1。乗務員データには異なる値を指定
        Map<String, Object> params = Map.of(
            "vehicleId", 100L,
            "dispatchCategory", 1,
            "members", List.of(Map.of(
                "vehicleId", 200L,
                "dispatchCategory", 2,
                "employeeId", 50L,
                "role", 1,
                "displayOrder", 1,
                "state", 0
            ))
        );

        service.saveMembers(params, "test");

        // 実際にINSERTへ渡されたデータを検証
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
        assertEquals(State.INITIAL.getCode(), saved.get("state"));
    }
}