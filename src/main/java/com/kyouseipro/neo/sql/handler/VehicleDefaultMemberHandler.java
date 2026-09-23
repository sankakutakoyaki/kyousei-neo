package com.kyouseipro.neo.sql.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleDefaultMemberHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.VEHICLE_DEFAULT_MEMBER_SAVE;
    }

    @Override
    @Transactional
    public Object execute(
            QueryDefinition def,
            SelectRequest req) {

        Map<String, Object> params =
            req.getParams();

        String editor =
            String.valueOf(
                params.get("editor")
            );

        int count = 0;

        // =========================
        // 削除
        // =========================
        count += deleteMembers(
            params,
            editor
        );

        // =========================
        // 追加・更新
        // =========================
        count += saveMembers(
            params,
            editor
        );

        return Map.of(
            "count",
            count
        );
    }

    private int deleteMembers(
            Map<String, Object> params,
            String editor) {

        Object value =
            params.get("deletedMembers");

        if (!(value instanceof List<?> list)) {
            return 0;
        }

        int count = 0;

        for (Object item : list) {

            if (!(item instanceof Map<?, ?> source)) {
                continue;
            }

            Long id =
                toLong(
                    source.get(
                        "vehicleDefaultMemberId"
                    )
                );

            if (id == null) {
                continue;
            }

            Map<String, Object> row =
                new LinkedHashMap<>();

            row.put(
                "vehicleDefaultMemberId",
                id
            );

            row.put(
                "version",
                source.get("version")
            );

            row.put(
                "state",
                State.DELETE.getCode()
            );

            count +=
                baseRepository.update(
                    Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS,
                    row,
                    editor
                );
        }

        return count;
    }

    private int saveMembers(
            Map<String, Object> params,
            String editor) {

        Object value =
            params.get("members");

        if (!(value instanceof List<?> list)) {
            return 0;
        }

        int count = 0;

        for (Object item : list) {

            if (!(item instanceof Map<?, ?> source)) {
                continue;
            }

            Long id =
                toLong(
                    source.get(
                        "vehicleDefaultMemberId"
                    )
                );

            Map<String, Object> row =
                new LinkedHashMap<>();

            row.put(
                "vehicleId",
                source.get("vehicleId")
            );

            row.put(
                "dispatchCategory",
                source.get(
                    "dispatchCategory"
                )
            );

            row.put(
                "employeeId",
                source.get("employeeId")
            );

            row.put(
                "role",
                source.get("role")
            );

            row.put(
                "displayOrder",
                source.get(
                    "displayOrder"
                )
            );

            row.put(
                "state",
                source.get("state")
            );


            // =========================
            // 新規
            // =========================
            if (id == null) {

                baseRepository.insert(
                    Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS,
                    row,
                    editor
                );

                count++;

                continue;
            }


            // =========================
            // 更新
            // =========================
            row.put(
                "vehicleDefaultMemberId",
                id
            );

            row.put(
                "version",
                source.get("version")
            );

            count +=
                baseRepository.update(
                    Tables.VEHICLE_DEFAULT_MEMBER_BY_IDS,
                    row,
                    editor
                );
        }

        return count;
    }

    private static Long toLong(
            Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        String text =
            value.toString().trim();

        if (text.isEmpty()) {
            return null;
        }

        return Long.valueOf(text);
    }
}