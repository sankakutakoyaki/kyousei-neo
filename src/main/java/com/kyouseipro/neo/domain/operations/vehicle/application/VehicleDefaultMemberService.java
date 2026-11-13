package com.kyouseipro.neo.domain.operations.vehicle.application;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.util.ValueUtil;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleDefaultMemberService {

    private final BaseSqlRepository baseRepository;
    private final SqlRepository sqlRepository;

    /**
     * 車両の基本配車区分を登録・更新する
     */
    public int saveDispatchCategory(Map<String, Object> params, String editor) {
        Long vehicleId = ValueUtil.toLong(params.get("vehicleId"));
        Integer dispatchCategory = ValueUtil.toInt(params.get("dispatchCategory"));

        if(vehicleId == null || dispatchCategory == null || dispatchCategory == 0){
            throw new IllegalArgumentException("配車区分を選択してください");
        }

        List<Map<String, Object>> rows = sqlRepository.selectMap(
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

        // 未登録の場合は新規作成
        if(rows.isEmpty()){
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vehicleId", vehicleId);
            row.put("dispatchCategory", dispatchCategory);
            row.put("state", State.INITIAL.getCode());
            baseRepository.insert(Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS, row, editor);
            return 1;
        }

        Map<String, Object> current = rows.get(0);
        Integer currentCategory = ValueUtil.toInt(current.get("dispatchCategory"));

        // 区分が変わっていなければ更新しない
        if(Objects.equals(currentCategory, dispatchCategory)){
            return 0;
        }

        // 既存区分の更新
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("vehicleDispatchCategoryId", current.get("vehicleDispatchCategoryId"));
        row.put("dispatchCategory", dispatchCategory);
        row.put("version", current.get("version"));

        // 基本配車区分を更新
        int count = baseRepository.update(
            Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS, row, editor
        );

        // 登録済み基本乗務員も新しい区分へ引き継ぐ
        List<Map<String, Object>> members = sqlRepository.selectMap(
            """
            SELECT vehicle_default_member_id, version
            FROM vehicle_default_members
            WHERE vehicle_id = ? AND dispatch_category = ? AND state = ?
            """,
            List.of(vehicleId, currentCategory, State.INITIAL.getCode())
        );

        for(Map<String, Object> member : members){
            Map<String, Object> update = new LinkedHashMap<>();
            update.put("vehicleDefaultMemberId", member.get("vehicleDefaultMemberId"));
            update.put("version", member.get("version"));
            update.put("dispatchCategory", dispatchCategory);

            count += baseRepository.update(
                Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS, update, editor
            );
        }

        return count;
    }

    /**
     * 基本配車設定を解除する（基本乗務員も論理削除）
     */
    public int clearDispatchSettings(Long vehicleId, String editor) {
        if(vehicleId == null || vehicleId <= 0){
            throw new IllegalArgumentException("車両IDが指定されていません。");
        }

        // 有効な基本配車区分を取得
        List<Map<String, Object>> categories = sqlRepository.selectMap(
            """
            SELECT vehicle_dispatch_category_id, version
            FROM vehicle_dispatch_categories
            WHERE vehicle_id = ? AND state = ?
            """,
            List.of(vehicleId, State.INITIAL.getCode())
        );

        // 区分が未設定なら解除処理は不要
        if(categories.isEmpty()) return 0;
        if(categories.size() > 1){
            throw new IllegalStateException("基本配車区分が重複登録されています。");
        }

        // 登録済みの基本乗務員を取得
        List<Map<String, Object>> members = sqlRepository.selectMap(
            """
            SELECT vehicle_default_member_id, version
            FROM vehicle_default_members
            WHERE vehicle_id = ? AND state = ?
            """,
            List.of(vehicleId, State.INITIAL.getCode())
        );

        int count = 0;

        // 基本乗務員を先に論理削除
        for(Map<String, Object> member : members){
            Map<String, Object> row = new HashMap<>();
            row.put("vehicleDefaultMemberId", member.get("vehicleDefaultMemberId"));
            row.put("version", member.get("version"));

            count += baseRepository.delete(
                Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS, row, editor
            );
        }

        // 基本配車区分を論理削除
        Map<String, Object> category = categories.get(0);
        Map<String, Object> row = new HashMap<>();
        row.put("vehicleDispatchCategoryId", category.get("vehicleDispatchCategoryId"));
        row.put("version", category.get("version"));

        count += baseRepository.delete(
            Tables.VEHICLE_DISPATCH_CATEGORY_BY_IDS, row, editor
        );

        return count;
    }

    public int deleteMembers(Map<String, Object> params, String editor) {
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

    public int saveMembers(Map<String, Object> params, String editor) {
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

            // 既存乗務員は現在の登録内容と比較する
            if(id != null){
                validateMemberVehicle(id, vehicleId);

                List<Map<String, Object>> rows = sqlRepository.selectMap(
                    """
                    SELECT role, display_order, version
                    FROM vehicle_default_members
                    WHERE vehicle_default_member_id = ? AND state = ?
                    """,
                    List.of(id, State.INITIAL.getCode())
                );
                if(rows.isEmpty()){
                    throw new IllegalArgumentException("基本乗務員が見つかりません。");
                }

                Map<String, Object> current = rows.get(0);
                boolean changed =
                    !Objects.equals(ValueUtil.toInt(current.get("role")), ValueUtil.toInt(source.get("role")))
                    || !Objects.equals(ValueUtil.toInt(current.get("displayOrder")), ValueUtil.toInt(source.get("displayOrder")));

                // 変更がなければUPDATEしない
                if(!changed){
                    continue;
                }
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("vehicleId", vehicleId);
            row.put("dispatchCategory", dispatchCategory);
            row.put("employeeId", source.get("employeeId"));
            row.put("role", source.get("role"));
            row.put("displayOrder", source.get("displayOrder"));

            // 新規登録時だけ有効状態を設定
            if (id == null) {
                row.put("state", State.INITIAL.getCode());
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

    /**
     * 車両フォームの統合保存用：新規乗務員だけを登録する
     */
    public int saveNewMembers(Map<String, Object> params, String editor) {
        Object value = params.get("members");
        if(!(value instanceof List<?> list)) return 0;

        // 登録済みの乗務員はUPDATEせず、新規分だけ抽出
        List<?> newMembers = list.stream()
            .filter(item -> item instanceof Map<?, ?> source
                && source.get("vehicleDefaultMemberId") == null)
            .toList();

        if(newMembers.isEmpty()) return 0;

        Map<String, Object> saveParams = new LinkedHashMap<>(params);
        saveParams.put("members", newMembers);
        return saveMembers(saveParams, editor);
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