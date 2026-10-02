package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.dispatch.DailyCrewMemberQuery;
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
            || kind == QueryKind.DAILY_CREW_REINITIALIZE
            || kind == QueryKind.DAILY_CREW_MEMBER_ADD
            || kind == QueryKind.DAILY_CREW_MEMBER_SAVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (def.getKind()) {
            case DAILY_CREW_BULK_CREATE -> executeBulkCreate(req);
            case DAILY_CREW_REINITIALIZE -> executeReinitialize(req);
            case DAILY_CREW_MEMBER_ADD -> executeMemberAdd(req);
            case DAILY_CREW_MEMBER_SAVE -> executeMemberSave(req);
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
            checkParams.put("state", State.INITIAL.getCode());

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
            row.put("state", State.INITIAL.getCode());

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
                memberParams.put("state", State.INITIAL.getCode());

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
                    member.put("state", State.INITIAL.getCode());

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
        int state = State.INITIAL.getCode();

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
            update.put("state", State.DELETE.getCode());
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
            update.put("state", State.DELETE.getCode());
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

    private Object executeMemberAdd(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        int dailyCrewId = ((Number) params.get("dailyCrewId")).intValue();
        int employeeId = ((Number) params.get("employeeId")).intValue();
        int role = ((Number) params.getOrDefault("role", 2)).intValue();

        // 既存チェック
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("dailyCrewId", dailyCrewId);
        checkParams.put("employeeId", employeeId);
        SelectRequest checkReq = new SelectRequest();
        checkReq.setParams(
            checkParams
        );

        List<Map<String, Object>> exists = queryExecutor.select(DailyCrewMemberQuery.dailyCrewMemberExists(), checkReq);

        if (!exists.isEmpty()) {
            Map<String, Object> existing =  exists.get(0);
            int state = ((Number) existing.get("state")).intValue();
            // 論理削除済みなら再有効化
            if (state == State.DELETE.getCode()) {
                Long id = 
                    baseRepository.reactivate(Tables.DAILY_CREW_MEMBER_BY_IDS, existing.get("dailyCrewMemberId"), existing.get("version"), editor
                );
                return Map.of( "id", id);
            }
            throw new BusinessException("この乗務員はすでに班に追加されています。");
        }

        // displayOrder
        Map<String, Object> orderParams = new HashMap<>();
        orderParams.put("dailyCrewId", dailyCrewId);
        orderParams.put("state", State.INITIAL.getCode());

        SelectRequest orderReq = new SelectRequest();
        orderReq.setParams(orderParams);

        List<Map<String, Object>> orderRows = queryExecutor.select(DailyCrewMemberQuery.dailyCrewMemberMaxDisplayOrder(), orderReq);
        int displayOrder = 1;
        if (!orderRows.isEmpty()) {
            Object value = orderRows.get(0).get("maxDisplayOrder");
            if (value != null) {
                displayOrder = ((Number) value).intValue() + 1;
            }
        }

        // INSERT
        Map<String, Object> row = new HashMap<>();
        row.put("dailyCrewId", dailyCrewId);
        row.put("employeeId", employeeId);
        row.put("role", role);
        row.put("displayOrder", displayOrder);
        row.put("state", State.INITIAL.getCode());

        Long id = baseRepository.insert(Tables.DAILY_CREW_MEMBER_BY_IDS, row, editor);
        return Map.of("id", id);
    }

    @SuppressWarnings("unchecked")
    private Object executeMemberSave(
            SelectRequest req
    ) {

        Map<String, Object> params =
            req.getParams();

        String editor =
            (String) params.getOrDefault(
                "editor",
                "system"
            );

        long dailyCrewId =
            ((Number) params.get(
                "dailyCrewId"
            )).longValue();

        List<Map<String, Object>> members =
            (List<Map<String, Object>>)
                params.getOrDefault(
                    "members",
                    List.of()
                );


        // =========================================
        // 現在のDB状態を取得
        // 削除済みも含む
        // =========================================

        Map<String, Object> currentParams =
            new HashMap<>();

        currentParams.put(
            "dailyCrewId",
            dailyCrewId
        );

        SelectRequest currentReq =
            new SelectRequest();

        currentReq.setParams(
            currentParams
        );

        List<Map<String, Object>> currentRows =
            queryExecutor.select(
                DailyCrewMemberQuery
                    .dailyCrewMemberSaveList(),
                currentReq
            );


        // employeeId → DB行
        Map<Long, Map<String, Object>>
            currentByEmployee =
                new HashMap<>();

        for(
            Map<String, Object> row
                : currentRows
        ){
            long employeeId =
                ((Number) row.get(
                    "employeeId"
                )).longValue();

            currentByEmployee.put(
                employeeId,
                row
            );
        }


        // =========================================
        // 画面側の乗務員
        // =========================================

        Map<Long, Map<String, Object>>
            selectedByEmployee =
                new LinkedHashMap<>();

        for(
            Map<String, Object> member
                : members
        ){
            long employeeId =
                ((Number) member.get(
                    "employeeId"
                )).longValue();

            // 同一社員の重複チェック
            if(
                selectedByEmployee
                    .containsKey(employeeId)
            ){
                throw new BusinessException(
                    "同じ乗務員が重複しています。"
                );
            }

            selectedByEmployee.put(
                employeeId,
                member
            );
        }


        // =========================================
        // 1. フォームから消えた既存乗務員を削除
        // =========================================

        for(
            Map<String, Object> current
                : currentRows
        ){

            long employeeId =
                ((Number) current.get(
                    "employeeId"
                )).longValue();

            int state =
                ((Number) current.get(
                    "state"
                )).intValue();

            if(
                state ==
                State.DELETE.getCode()
            ){
                continue;
            }

            if(
                selectedByEmployee
                    .containsKey(employeeId)
            ){
                continue;
            }

            Map<String, Object> update =
                new HashMap<>();

            update.put(
                "dailyCrewMemberId",
                current.get(
                    "dailyCrewMemberId"
                )
            );

            update.put(
                "version",
                current.get(
                    "version"
                )
            );

            update.put(
                "state",
                State.DELETE.getCode()
            );

            baseRepository.update(
                Tables.DAILY_CREW_MEMBER_BY_IDS,
                update,
                editor
            );
        }


        // =========================================
        // 新規追加時のdisplayOrder
        // =========================================

        int nextDisplayOrder =
            currentRows.stream()
                .filter(
                    row ->
                        ((Number) row.get("state"))
                            .intValue()
                        != State.DELETE.getCode()
                )
                .map(
                    row ->
                        (Number) row.get(
                            "displayOrder"
                        )
                )
                .filter(
                    value ->
                        value != null
                )
                .mapToInt(
                    Number::intValue
                )
                .max()
                .orElse(0)
                + 1;


        // =========================================
        // 2. フォーム側の乗務員を反映
        // =========================================

        for(
            Map<String, Object> selected
                : members
        ){

            long employeeId =
                ((Number) selected.get(
                    "employeeId"
                )).longValue();

            int role =
                ((Number) selected.get(
                    "role"
                )).intValue();

            Map<String, Object> current =
                currentByEmployee.get(
                    employeeId
                );


            // -------------------------------------
            // 完全な新規
            // -------------------------------------

            if(current == null){

                Map<String, Object> insert =
                    new HashMap<>();

                insert.put(
                    "dailyCrewId",
                    dailyCrewId
                );

                insert.put(
                    "employeeId",
                    employeeId
                );

                insert.put(
                    "role",
                    role
                );

                insert.put(
                    "displayOrder",
                    nextDisplayOrder++
                );

                insert.put(
                    "state",
                    State.INITIAL.getCode()
                );

                baseRepository.insert(
                    Tables.DAILY_CREW_MEMBER_BY_IDS,
                    insert,
                    editor
                );

                continue;
            }


            int state =
                ((Number) current.get(
                    "state"
                )).intValue();

            int currentRole =
                ((Number) current.get(
                    "role"
                )).intValue();


            // -------------------------------------
            // 過去に削除済み → 復活
            // -------------------------------------

            if(
                state ==
                State.DELETE.getCode()
            ){

                baseRepository.reactivate(
                    Tables.DAILY_CREW_MEMBER_BY_IDS,
                    current.get(
                        "dailyCrewMemberId"
                    ),
                    current.get(
                        "version"
                    ),
                    editor
                );


                /*
                * roleまで変更されている場合だけ、
                * 復活後のversionを再取得して更新する。
                */
                if(
                    currentRole != role
                ){

                    Map<String, Object> checkParams =
                        new HashMap<>();

                    checkParams.put(
                        "dailyCrewId",
                        dailyCrewId
                    );

                    checkParams.put(
                        "employeeId",
                        employeeId
                    );

                    SelectRequest checkReq =
                        new SelectRequest();

                    checkReq.setParams(
                        checkParams
                    );

                    List<Map<String, Object>> refreshed =
                        queryExecutor.select(
                            DailyCrewMemberQuery
                                .dailyCrewMemberExists(),
                            checkReq
                        );

                    if(refreshed.isEmpty()){
                        throw new BusinessException(
                            "乗務員の再登録に失敗しました。"
                        );
                    }

                    Map<String, Object> refreshedRow =
                        refreshed.get(0);

                    Map<String, Object> update =
                        new HashMap<>();

                    update.put(
                        "dailyCrewMemberId",
                        refreshedRow.get(
                            "dailyCrewMemberId"
                        )
                    );

                    update.put(
                        "version",
                        refreshedRow.get(
                            "version"
                        )
                    );

                    update.put(
                        "role",
                        role
                    );

                    baseRepository.update(
                        Tables
                            .DAILY_CREW_MEMBER_BY_IDS,
                        update,
                        editor
                    );
                }

                continue;
            }


            // -------------------------------------
            // 既存乗務員のrole変更
            // -------------------------------------

            if(
                currentRole != role
            ){

                Map<String, Object> update =
                    new HashMap<>();

                update.put(
                    "dailyCrewMemberId",
                    current.get(
                        "dailyCrewMemberId"
                    )
                );

                update.put(
                    "version",
                    current.get(
                        "version"
                    )
                );

                update.put(
                    "role",
                    role
                );

                baseRepository.update(
                    Tables.DAILY_CREW_MEMBER_BY_IDS,
                    update,
                    editor
                );
            }
        }


        return Map.of(
            "count",
            members.size()
        );
    }
}