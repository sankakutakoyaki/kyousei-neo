package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.dispatch.DailyCrewQuery;
import com.kyouseipro.neo.sql.query.operations.vehicle.VehicleDefaultMemberQuery;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.service.QueryExecutor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DispatchHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
    private final QueryExecutor queryExecutor;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.DAILY_CREW_BULK_CREATE;
    }

    @Override
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (def.getKind()) {
            case DAILY_CREW_BULK_CREATE -> executeBulkCreate(req);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private Object executeBulkCreate(SelectRequest req) {

        Map<String, Object> params = req.getParams();

        String editor =
            (String) params.getOrDefault(
                "editor",
                "system"
            );

        String workDate =
            String.valueOf(
                params.get("workDate")
            );

        int officeId =
            ((Number) params.get("officeId"))
                .intValue();

        int dispatchCategory =
            ((Number) params.get("dispatchCategory"))
                .intValue();

        List<Number> vehicleIds =
            (List<Number>) params.get("vehicleIds");

        int count = 0;
        int skipped = 0;
        int memberCount = 0;
        int displayOrder = 1;


        for (Number vehicleIdValue : vehicleIds) {

            int vehicleId =
                vehicleIdValue.intValue();


            // -----------------------------
            // 既存の当日班を確認
            // -----------------------------

            Map<String, Object> checkParams =
                new HashMap<>();

            checkParams.put(
                "workDate",
                workDate
            );

            checkParams.put(
                "officeId",
                officeId
            );

            checkParams.put(
                "dispatchCategory",
                dispatchCategory
            );

            checkParams.put(
                "vehicleId",
                vehicleId
            );

            checkParams.put(
                "state",
                0
            );


            SelectRequest checkReq =
                new SelectRequest();

            checkReq.setParams(
                checkParams
            );


            List<Map<String, Object>> exists =
                queryExecutor.select(
                    DailyCrewQuery.dailyCrewExists(),
                    checkReq
                );


            // 既に作られている班は触らない
            if (!exists.isEmpty()) {
                skipped++;
                continue;
            }


            // -----------------------------
            // daily_crews 作成
            // -----------------------------

            Map<String, Object> row =
                new HashMap<>();

            row.put(
                "workDate",
                workDate
            );

            row.put(
                "officeId",
                officeId
            );

            row.put(
                "dispatchCategory",
                dispatchCategory
            );

            row.put(
                "vehicleId",
                vehicleId
            );

            row.put(
                "displayOrder",
                displayOrder++
            );

            row.put(
                "state",
                0
            );


            baseRepository.insert(
                Tables.DAILY_CREW_BY_IDS,
                row,
                editor
            );

            count++;


            // -----------------------------
            // 作成した dailyCrewId を取得
            // -----------------------------

            List<Map<String, Object>> created =
                queryExecutor.select(
                    DailyCrewQuery.dailyCrewExists(),
                    checkReq
                );

            if (created.isEmpty()) {
                throw new IllegalStateException(
                    "作成した配車班を取得できません。"
                );
            }


            int dailyCrewId =
                ((Number) created
                    .get(0)
                    .get("dailyCrewId"))
                    .intValue();


            // -----------------------------
            // 車両の基本乗務員を取得
            // -----------------------------

            Map<String, Object> memberParams =
                new HashMap<>();

            memberParams.put(
                "vehicleId",
                vehicleId
            );

            memberParams.put(
                "dispatchCategory",
                dispatchCategory
            );

            memberParams.put(
                "state",
                0
            );


            SelectRequest memberReq =
                new SelectRequest();

            memberReq.setParams(
                memberParams
            );


            List<Map<String, Object>> defaultMembers =
                queryExecutor.select(
                    VehicleDefaultMemberQuery
                        .vehicleDefaultMemberList(),
                    memberReq
                );


            // -----------------------------
            // daily_crew_members 作成
            // -----------------------------

            for (
                Map<String, Object> source
                    : defaultMembers
            ) {

                Map<String, Object> member =
                    new HashMap<>();

                member.put(
                    "dailyCrewId",
                    dailyCrewId
                );

                member.put(
                    "employeeId",
                    source.get("employeeId")
                );

                member.put(
                    "role",
                    source.get("role")
                );

                member.put(
                    "displayOrder",
                    source.get("displayOrder")
                );

                member.put(
                    "state",
                    0
                );


                baseRepository.insert(
                    Tables.DAILY_CREW_MEMBER_BY_IDS,
                    member,
                    editor
                );

                memberCount++;
            }
        }


        return Map.of(
            "count", count,
            "memberCount", memberCount,
            "skipped", skipped
        );
    }
}