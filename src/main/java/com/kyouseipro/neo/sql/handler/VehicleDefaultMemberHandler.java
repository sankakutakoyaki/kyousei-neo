package com.kyouseipro.neo.sql.handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.util.ValueUtil;
import com.kyouseipro.neo.domain.operations.vehicle.application.VehicleDefaultMemberService;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.security.PermissionService;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleDefaultMemberHandler implements QueryHandler {

    private final VehicleDefaultMemberService vehicleDefaultMemberService;
    // 基本乗務員の更新権限を確認する
    private final PermissionService permissionService;
    // 登録済みの基本配車区分を確認
    private final SqlRepository sqlRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.VEHICLE_DEFAULT_MEMBER_SAVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = String.valueOf(params.get("editor"));
        // 基本配車区分の変更によって乗務員が更新されるか確認
        Long vehicleId = ValueUtil.toLong(params.get("vehicleId"));
        Integer nextCategory = ValueUtil.toInt(params.get("dispatchCategory"));

        if(vehicleId != null && nextCategory != null && nextCategory > 0){
            List<Map<String, Object>> rows = sqlRepository.selectMap(
                """
                SELECT dispatch_category
                FROM vehicle_dispatch_categories
                WHERE vehicle_id = ? AND state = 0
                """,
                List.of(vehicleId)
            );

            if(rows.size() > 1){
                throw new IllegalStateException("基本配車区分が重複登録されています。");
            }

            Integer currentCategory = rows.isEmpty()
                ? null : ValueUtil.toInt(rows.get(0).get("dispatchCategory"));

            // 基本配車区分を新規登録・変更する場合の権限確認
            if(!Objects.equals(currentCategory, nextCategory)){
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String categoryAction = rows.isEmpty() ? "insert" : "update";

                if(!permissionService.canTable(auth, "vehicle_dispatch_categories", categoryAction)){
                    throw new SecurityException("基本配車区分の保存権限がありません。");
                }
            }

            // 既存区分を変更すると乗務員も更新されるため権限を確認
            if(currentCategory != null && !Objects.equals(currentCategory, nextCategory)){
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if(!permissionService.canTable(auth, "vehicle_default_members", "update")){
                    throw new SecurityException("基本乗務員の更新権限がありません。");
                }
            }
        }

        // 基本乗務員の追加・削除・更新に必要な権限を確認
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean hasNew = params.get("members") instanceof List<?> members
            && members.stream().anyMatch(item ->
                item instanceof Map<?, ?> member
                && member.get("vehicleDefaultMemberId") == null
            );

        boolean hasDeleted = params.get("deletedMembers") instanceof List<?> deleted
            && !deleted.isEmpty();

        // 既存乗務員が含まれる場合は更新権限を確認
        boolean hasExisting = params.get("members") instanceof List<?> members
            && members.stream().anyMatch(item ->
                item instanceof Map<?, ?> member
                && member.get("vehicleDefaultMemberId") != null
            );

        if(hasNew && !permissionService.canTable(auth, "vehicle_default_members", "insert")){
            throw new SecurityException("基本乗務員の追加権限がありません。");
        }
        if(hasDeleted && !permissionService.canTable(auth, "vehicle_default_members", "delete")){
            throw new SecurityException("基本乗務員の削除権限がありません。");
        }
        if(hasExisting && !permissionService.canTable(auth, "vehicle_default_members", "update")){
            throw new SecurityException("基本乗務員の更新権限がありません。");
        }

        int count = 0;
        // 乗務員の削除・更新を先に実行し、versionの競合を防ぐ
        count += vehicleDefaultMemberService.deleteMembers(params, editor);
        count += vehicleDefaultMemberService.saveMembers(params, editor);
        count += vehicleDefaultMemberService.saveDispatchCategory(params, editor);
        return Map.of("count", count);
    }
}