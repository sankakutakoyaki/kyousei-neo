package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.exception.BusinessException;
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
public class DailyCrewHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
    private final QueryExecutor queryExecutor;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.DAILY_CREW_BULK_CREATE
            || kind == QueryKind.DAILY_CREW_REINITIALIZE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (def.getKind()) {
            case DAILY_CREW_BULK_CREATE -> executeBulkCreate(req);
            case DAILY_CREW_REINITIALIZE -> executeReinitialize(req);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private Object executeBulkCreate(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        String workDate = String.valueOf(params.get("workDate"));
        int officeId = ((Number) params.get("officeId")).intValue();
        int dispatchCategory = ((Number) params.get("dispatchCategory")).intValue();
        List<Number> vehicleIds = (List<Number>) params.get("vehicleIds");
        boolean copyDefaultMembers = !Boolean.FALSE.equals(params.get("copyDefaultMembers"));

        int count = 0;
        int skipped = 0;
        int memberCount = 0;
        int displayOrder = 1;

        for (Number vehicleIdValue : vehicleIds) {
            int vehicleId = vehicleIdValue.intValue();

            // 既存の当日班を確認
            Map<String, Object> checkParams = new HashMap<>();
            checkParams.put("workDate", workDate);
            checkParams.put("officeId", officeId);
            checkParams.put("dispatchCategory", dispatchCategory);
            checkParams.put("vehicleId", vehicleId);
            checkParams.put("state", 0);

            SelectRequest checkReq = new SelectRequest();
            checkReq.setParams(checkParams);
            List<Map<String, Object>> exists = queryExecutor.select(DailyCrewQuery.dailyCrewExists(), checkReq);
            // 既に作られている班は触らない
            if (!exists.isEmpty()) {
                skipped++;
                continue;
            }

            // daily_crews 作成
            Map<String, Object> row = new HashMap<>();
            row.put("workDate", workDate);
            row.put("officeId", officeId);
            row.put("dispatchCategory", dispatchCategory);
            row.put("vehicleId", vehicleId);
            row.put("displayOrder", displayOrder++);
            row.put("state", 0);

            baseRepository.insert(Tables.DAILY_CREW_BY_IDS, row, editor);
            count++;

            // 作成した dailyCrewId を取得
            List<Map<String, Object>> created = queryExecutor.select(
                DailyCrewQuery.dailyCrewExists(),
                checkReq
            );

            if (created.isEmpty()) {
                throw new BusinessException("作成した配車班を取得できません。");
            }
            int dailyCrewId = ((Number) created.get(0).get("dailyCrewId")).intValue();

            // 車両の基本乗務員を取得
            if (copyDefaultMembers) {
                Map<String, Object> memberParams = new HashMap<>();
                memberParams.put("vehicleId", vehicleId);
                memberParams.put("dispatchCategory", dispatchCategory);
                memberParams.put("state", 0);

                SelectRequest memberReq = new SelectRequest();
                memberReq.setParams(memberParams);

                List<Map<String, Object>> defaultMembers = 
                    queryExecutor.select(VehicleDefaultMemberQuery.vehicleDefaultMemberList(), memberReq);

                // daily_crew_members 作成
                for (Map<String, Object> source: defaultMembers) {
                    Map<String, Object> member = new HashMap<>();
                    member.put("dailyCrewId", dailyCrewId);
                    member.put("employeeId", source.get("employeeId"));
                    member.put("role", source.get("role"));
                    member.put("displayOrder", source.get("displayOrder"));
                    member.put("state", 0);

                    baseRepository.insert(Tables.DAILY_CREW_MEMBER_BY_IDS, member, editor);
                    memberCount++;
                }
            }
        }
        return Map.of(
            "count", count,
            "memberCount", memberCount,
            "skipped", skipped
        );
    }

    @SuppressWarnings("unchecked")
    private Object executeReinitialize(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        String workDate = String.valueOf(params.get("workDate"));
        int officeId = ((Number) params.get("officeId")).intValue();
        int dispatchCategory = ((Number) params.get("dispatchCategory")).intValue();
        int state = 0;

        // 1. 配車済みチェック
        Map<String, Object> countParams = new HashMap<>();
        countParams.put("state", state);
        countParams.put("workDate", workDate);
        countParams.put("officeId", officeId);
        countParams.put("dispatchCategory", dispatchCategory);
        SelectRequest countReq = new SelectRequest();
        countReq.setParams(countParams);
        List<Map<String, Object>> countRows = queryExecutor.select(DailyCrewQuery.dailyCrewAssignmentCount(), countReq);
        int assignmentCount = countRows.isEmpty() ? 0: ((Number) countRows.get(0).get("assignmentCount")).intValue();
        if (assignmentCount > 0) {
            throw new BusinessException("すでに配車されているため再初期化できません。");
        }

        // 2. 現在の当日班を取得
        Map<String, Object> boardParams = new HashMap<>();
        boardParams.put("workDate", workDate);
        boardParams.put("officeId", officeId);
        boardParams.put("dispatchCategory", dispatchCategory);
        boardParams.put("state", state);
        SelectRequest boardReq = new SelectRequest();
        boardReq.setParams(boardParams);
        List<Map<String, Object>> rows = queryExecutor.select(DailyCrewQuery.dailyCrewBoardList(), boardReq);

        // 3. daily_crew_members を論理削除
        Map<Long, Integer> memberVersions = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object idValue = row.get("dailyCrewMemberId");
            if (idValue == null) {
                continue;
            }
            long id = ((Number) idValue).longValue();
            int version = ((Number) row.get("memberVersion")).intValue();
            memberVersions.put(id, version);
        }

        for (Map.Entry<Long, Integer> entry : memberVersions.entrySet()) {
            Map<String, Object> update = new HashMap<>();
            update.put("dailyCrewMemberId", entry.getKey());
            update.put("version", entry.getValue());
            update.put("state", 9);
            baseRepository.update(Tables.DAILY_CREW_MEMBER_BY_IDS, update, editor);
        }

        // 4. daily_crews を論理削除
        Map<Long, Integer> crewVersions = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            long id = ((Number) row.get("dailyCrewId")).longValue();
            int version = ((Number) row.get("dailyCrewVersion")).intValue();
            crewVersions.put(id, version);
        }

        for (Map.Entry<Long, Integer> entry : crewVersions.entrySet()) {
            Map<String, Object> update = new HashMap<>();
            update.put("dailyCrewId", entry.getKey());
            update.put("version", entry.getValue());
            update.put("state", 9);
            baseRepository.update(Tables.DAILY_CREW_BY_IDS, update, editor);
        }

        // 5. 最新の基本設定＋出勤者から車両取得
        Map<String, Object> defaultParams = new HashMap<>();
        defaultParams.put("workDate", workDate);
        defaultParams.put("officeId", officeId);
        defaultParams.put("dispatchCategory", dispatchCategory);
        defaultParams.put("shiftType", 1);
        defaultParams.put("state", state);
        SelectRequest defaultReq = new SelectRequest();
        defaultReq.setParams(defaultParams);

        List<Map<String, Object>> defaultRows =
            queryExecutor.select(VehicleDefaultMemberQuery.vehicleDispatchDefaultList(), defaultReq);

        List<Integer> vehicleIds = defaultRows.stream().map(row ->
            ((Number) row.get("vehicleId")).intValue()).distinct().toList();


        // 6. 最新設定から再生成
        if (!vehicleIds.isEmpty()) {
            Map<String, Object> createParams = new HashMap<>();
            createParams.put("workDate", workDate);
            createParams.put("officeId", officeId);
            createParams.put("dispatchCategory", dispatchCategory);
            createParams.put("vehicleIds", vehicleIds);
            createParams.put("editor", editor);
            createParams.put("copyDefaultMembers", true);
            
            SelectRequest createReq = new SelectRequest();
            createReq.setParams(createParams);

            return executeBulkCreate(createReq);
        }

        return Map.of(
            "count", 0,
            "memberCount", 0,
            "skipped", 0
        );
    }
}