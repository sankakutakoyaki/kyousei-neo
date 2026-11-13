package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.enums.system.SqlMode;
import com.kyouseipro.neo.common.util.ValueUtil;
import com.kyouseipro.neo.domain.operations.vehicle.application.VehicleDefaultMemberService;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.security.PermissionService;
import com.kyouseipro.neo.sql.common.SqlBuilder;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.model.TableMeta;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.service.SqlColumnValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleSettingsSaveHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
    private final SqlColumnValidator columnValidator;
    private final PermissionService permissionService;
    // 基本配車区分・基本乗務員の保存処理を共通Serviceで実行
    private final VehicleDefaultMemberService vehicleDefaultMemberService;
    // 現在の基本配車区分を取得する
    private final SqlRepository sqlRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.VEHICLE_SETTINGS_SAVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        Map<String, Object> params = req.getParams();
        TableMeta meta = def.getTableMeta();
        String editor = String.valueOf(params.get("editor"));

        // 車両情報と基本配車設定のデータを分離
        Map<String, Object> vehicle = new HashMap<>(params);
        vehicle.keySet().removeAll(Set.of(
            "dispatchCategory", "members", "deletedMembers",
            "memberDispatchCategory", "isNew", "editor", "state"
        ));

        Object idValue = vehicle.get(meta.idColumn());
        boolean isInsert = idValue == null || Long.parseLong(idValue.toString()) == 0;

        // 車両の登録・更新権限を確認
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 管理項目と実在カラムを検証
        SqlBuilder.validateSystemColumns(vehicle, isInsert ? SqlMode.INSERT : SqlMode.UPDATE);
        Set<String> columns = new HashSet<>(vehicle.keySet());
        columns.remove(meta.idColumn());
        columns.remove(meta.versionColumn());
        columnValidator.validate(meta.tableName(), columns);

        // 車両情報の保存が必要か判定
        boolean saveVehicle = isInsert || !columns.isEmpty();

        // 車両情報を保存する場合だけテーブル権限を確認
        if(saveVehicle && !permissionService.canTable(
                authentication, meta.tableName(), isInsert ? "insert" : "update")){
            throw new SecurityException("車両情報の保存権限がありません。");
        }

        Long vehicleId;
        int count;

        if(isInsert){
            // 新規車両を登録して採番IDを取得
            vehicle.remove(meta.versionColumn());
            vehicle.put("state", State.INITIAL.getCode());
            vehicleId = baseRepository.insert(meta, vehicle, editor);
            count = 1;
        }else{
            vehicleId = Long.valueOf(idValue.toString());
            if(!vehicle.containsKey(meta.versionColumn())){
                throw new IllegalArgumentException("車両のversionが指定されていません。");
            }

            // 車両情報を更新しない場合も、存在とversionを確認する
            if(columns.isEmpty()){
                var rows = sqlRepository.selectMap(
                    """
                    SELECT version
                    FROM vehicles
                    WHERE vehicle_id = ? AND state = ?
                    """,
                    List.of(vehicleId, State.INITIAL.getCode())
                );

                if(rows.isEmpty()){
                    throw new IllegalArgumentException("車両が見つからないか、削除されています。");
                }

                Long currentVersion = ValueUtil.toLong(rows.get(0).get("version"));
                Long requestVersion = ValueUtil.toLong(vehicle.get(meta.versionColumn()));

                if(!Objects.equals(currentVersion, requestVersion)){
                    throw new IllegalStateException("車両情報が他のユーザーによって更新されています。再読み込みしてください。");
                }
            }

            // 車両項目が含まれる場合だけ更新する
            if(columns.isEmpty()){
                count = 0;
            }else{
                count = baseRepository.update(meta, vehicle, editor);
            }
        }

        // 基本乗務員の追加・削除に車両IDを引き継ぐ
        Map<String, Object> settings = new HashMap<>(params);
        settings.put("vehicleId", vehicleId);

        // 新規乗務員の有無を確認
        boolean hasNewMembers = params.get("members") instanceof List<?> members
            && members.stream().anyMatch(item ->
                item instanceof Map<?, ?> member
                && member.get("vehicleDefaultMemberId") == null
            );

        // 基本乗務員を追加する場合は基本配車区分を必須とする
        Integer selectedCategory = ValueUtil.toInt(params.get("dispatchCategory"));
        if(hasNewMembers && (selectedCategory == null || selectedCategory <= 0)){
            throw new IllegalArgumentException("基本乗務員を追加する場合は基本配車区分を選択してください。");
        }

        // 削除対象の有無を確認
        boolean hasDeletedMembers = params.get("deletedMembers") instanceof List<?> deleted
            && !deleted.isEmpty();

        // 基本配車区分を明示的に未選択（0）にした場合は設定を解除
        // selectedCategoryは上で取得済みなので再利用する
        if(params.containsKey("dispatchCategory")
                && (selectedCategory == null || selectedCategory == 0)){

            // 登録済みの基本配車区分がある場合だけ解除
            var registered = sqlRepository.selectMap(
                """
                SELECT vehicle_dispatch_category_id
                FROM vehicle_dispatch_categories
                WHERE vehicle_id = ? AND state = ?
                """,
                List.of(vehicleId, State.INITIAL.getCode())
            );

            if(!registered.isEmpty()){
                // 区分と乗務員の両方に削除権限が必要
                if(!permissionService.canTable(authentication,
                        "vehicle_dispatch_categories", "delete")
                        || !permissionService.canTable(authentication,
                        "vehicle_default_members", "delete")){
                    throw new SecurityException("基本配車設定の解除権限がありません。");
                }

                // 基本配車区分と基本乗務員をまとめて論理削除
                count += vehicleDefaultMemberService.clearDispatchSettings(vehicleId, editor);
            }

            // 解除時は以降の乗務員追加・削除処理を実行しない
            return Map.of("data", vehicleId, "count", count);
        }

        // 保存操作に応じた権限を確認
        if(hasNewMembers && !permissionService.canTable(
                authentication, "vehicle_default_members", "insert")){
            throw new SecurityException("基本乗務員の追加権限がありません。");
        }
        if(hasDeletedMembers && !permissionService.canTable(
                authentication, "vehicle_default_members", "delete")){
            throw new SecurityException("基本乗務員の削除権限がありません。");
        }
        // 既存乗務員は更新せず、新規追加・削除だけを実行
        if(hasDeletedMembers){
            count += vehicleDefaultMemberService.deleteMembers(settings, editor);
        }

        // 基本配車区分が指定されている場合だけ保存する
        Integer category = ValueUtil.toInt(params.get("dispatchCategory"));
        if(category != null && category > 0){
            // 登録済みの基本配車区分を確認
            var rows = sqlRepository.selectMap(
                """
                SELECT dispatch_category
                FROM vehicle_dispatch_categories
                WHERE vehicle_id = ? AND state = 0
                """,
                java.util.List.of(vehicleId)
            );

            if(rows.size() > 1){
                throw new IllegalStateException("基本配車区分が重複登録されています。");
            }

            Integer currentCategory = rows.isEmpty()
                ? null : ValueUtil.toInt(rows.get(0).get("dispatchCategory"));

            // 未登録または区分が変更された場合だけ保存
            if(!Objects.equals(currentCategory, category)){
                // 区分変更で既存乗務員も更新するため、更新権限を確認
                if(currentCategory != null && !permissionService.canTable(
                        authentication, "vehicle_default_members", "update")){
                    throw new SecurityException("基本乗務員の更新権限がありません。");
                }
                // 基本配車区分の登録・更新権限を確認
                String categoryAction = rows.isEmpty() ? "insert" : "update";

                if(!permissionService.canTable(
                        authentication, "vehicle_dispatch_categories", categoryAction)){
                    throw new SecurityException("基本配車区分の保存権限がありません。");
                }
                count += vehicleDefaultMemberService.saveDispatchCategory(settings, editor);
            }
        }

        if(hasNewMembers){
            count += vehicleDefaultMemberService.saveNewMembers(settings, editor);
        }

        return Map.of("data", vehicleId, "count", count);
    }
}
