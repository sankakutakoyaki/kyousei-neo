package com.kyouseipro.neo.sql.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.util.ValueUtil;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleDefaultMemberHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
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

        int count = 0;
        count += saveDispatchCategory(params, editor);
        count += deleteMembers(params, editor);
        count += saveMembers(params, editor);
        return Map.of("count", count);
    }

    private int deleteMembers(Map<String, Object> params, String editor) {
        Long vehicleId = ValueUtil.toLong(params.get("vehicleId"));

        Object value = params.get("deletedMembers");
        if (!(value instanceof List<?> list)) {
            return 0;
        }

        int count = 0;
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> source)) {
                continue;
            }
            Long id = ValueUtil.toLong(source.get("vehicleDefaultMemberId"));
            if (id == null) {
                continue;
            }

            // 所属車両の確認
            validateMemberVehicle(id, vehicleId);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vehicleDefaultMemberId", id);
            row.put("version", source.get("version"));
            row.put("state", State.DELETE.getCode());

            count += baseRepository.update(
                Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS, row, editor
            );
        }
        return count;
    }
    
    private int saveMembers(Map<String, Object> params, String editor) {
        Long vehicleId = ValueUtil.toLong(params.get("vehicleId"));
        Integer dispatchCategory = ValueUtil.toInt(params.get("dispatchCategory"));

        if (vehicleId == null || dispatchCategory == null || dispatchCategory == 0) {
            throw new IllegalArgumentException("車両または配車区分が指定されていません。");
        }

        Object value = params.get("members");
        if (!(value instanceof List<?> list)) {
            return 0;
        }

        int count = 0;
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> source)) {
                continue;
            }

            Long id = ValueUtil.toLong(source.get("vehicleDefaultMemberId"));

            // 既存レコードは所属車両を確認
            if (id != null) {
                validateMemberVehicle(id, vehicleId);
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vehicleId", vehicleId);
            row.put("dispatchCategory", dispatchCategory);
            row.put("employeeId", source.get("employeeId"));
            row.put("role", source.get("role"));
            row.put("displayOrder", source.get("displayOrder"));
            row.put("state", source.get("state"));

            // 新規登録
            if (id == null) {
                baseRepository.insert(Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS, row, editor);
                count++;
                continue;
            }

            // 更新
            row.put("vehicleDefaultMemberId", id);
            row.put("version", source.get("version"));
            count += baseRepository.update(
                Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS, row, editor
            );
        }
        return count;
    }

    private int saveDispatchCategory(Map<String, Object> params, String editor) {
        Long vehicleId = ValueUtil.toLong(params.get("vehicleId"));
        Integer dispatchCategory = ValueUtil.toInt(params.get("dispatchCategory"));
        if (vehicleId == null || dispatchCategory == null || dispatchCategory == 0) {
            throw new IllegalArgumentException("配車区分を選択してください");
        }

        List<Map<String, Object>> rows =
            sqlRepository.selectMap(
                """
                SELECT
                    vehicle_dispatch_category_id,
                    vehicle_id,
                    dispatch_category,
                    version
                FROM vehicle_dispatch_categories
                WHERE vehicle_id = ?
                AND state = ?
                """,
                List.of(vehicleId, State.INITIAL.getCode())
            );

        // 未登録
        if (rows.isEmpty()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vehicleId", vehicleId);
            row.put("dispatchCategory", dispatchCategory);
            row.put("state", State.INITIAL.getCode());
            baseRepository.insert(Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS, row, editor);
            return 1;
        }

        Map<String, Object> current = rows.get(0);
        Integer currentCategory = ValueUtil.toInt(current.get("dispatchCategory"));
        // 同じ区分なら何もしない
        if (Objects.equals(currentCategory, dispatchCategory)) {
            return 0;
        }
        // 区分変更
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("vehicleDispatchCategoryId", current.get("vehicleDispatchCategoryId"));
        row.put("dispatchCategory", dispatchCategory);
        row.put("version", current.get("version"));
        return baseRepository.update(Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS, row, editor);
    }

    private void validateMemberVehicle(Long memberId, Long vehicleId) {
        List<Map<String, Object>> rows = sqlRepository.selectMap(
            """
            SELECT vehicle_id
            FROM vehicle_default_members
            WHERE vehicle_default_member_id = ?
            """,
            List.of(memberId)
        );

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("既定乗務員が見つかりません。");
        }

        Long registeredVehicleId = ValueUtil.toLong(rows.get(0).get("vehicleId"));

        if (!Objects.equals(registeredVehicleId, vehicleId)) {
            throw new IllegalArgumentException(
                "指定された車両に属していない乗務員情報です。"
            );
        }
    }
}