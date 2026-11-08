package com.kyouseipro.neo.sql.handler;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.domain.operations.vehicle.application.VehicleDefaultMemberService;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleDefaultMemberHandler implements QueryHandler {

    private final VehicleDefaultMemberService vehicleDefaultMemberService;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.VEHICLE_DEFAULT_MEMBER_SAVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = String.valueOf(params.get("editor"));

        int count = 0;
        // 基本配車区分・基本乗務員の保存を共通Serviceへ委譲
        count += vehicleDefaultMemberService.saveDispatchCategory(params, editor);
        count += vehicleDefaultMemberService.deleteMembers(params, editor);
        count += vehicleDefaultMemberService.saveMembers(params, editor);
        return Map.of("count", count);
    }
}