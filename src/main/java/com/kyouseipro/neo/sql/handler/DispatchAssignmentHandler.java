package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository;
import com.kyouseipro.neo.domain.business.order.history.OrderHistoryRepository.DispatchEvent;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.query.operations.dispatch.DispatchAssignmentQuery;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.service.QueryExecutor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DispatchAssignmentHandler implements QueryHandler {
    private final BaseSqlRepository baseRepository;
    private final QueryExecutor queryExecutor;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.DISPATCH_ASSIGNMENT_CREATE
            || kind == QueryKind.DISPATCH_ASSIGNMENT_MOVE
            || kind == QueryKind.DISPATCH_ASSIGNMENT_REORDER
            || kind == QueryKind.DISPATCH_ASSIGNMENT_RESET
            || kind == QueryKind.DISPATCH_ASSIGNMENT_REMOVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch (def.getKind()) {
            case DISPATCH_ASSIGNMENT_CREATE -> executeCreate(req);
            case DISPATCH_ASSIGNMENT_MOVE -> executeMove(req);
            case DISPATCH_ASSIGNMENT_REORDER -> executeAssignmentReorder(req);
            case DISPATCH_ASSIGNMENT_RESET -> executeReset(req);
            case DISPATCH_ASSIGNMENT_REMOVE -> executeRemove(req);
            default -> throw new IllegalStateException();
        };
    }

    private Object executeCreate(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        int orderId = ((Number) params.get("orderId")).intValue();
        int dailyCrewId = ((Number) params.get("dailyCrewId")).intValue();
        String editor = (String) params.getOrDefault("editor", "system");
        // ① 対象班をロック
        lockCrew(dailyCrewId);
        // ② 班内の次の訪問順序を取得
        int visitOrder = getNextVisitOrder(dailyCrewId);
        // ③ 配車割当を登録
        Map<String, Object> insert = new HashMap<>();
        insert.put("orderId", orderId);
        insert.put("dailyCrewId", dailyCrewId);
        insert.put("visitOrder", visitOrder);
        insert.put("remarks", params.getOrDefault("remarks", ""));
        insert.put("state", State.INITIAL.getCode());
        Long id = baseRepository.insert(Tables.DISPATCH_ASSIGNMENT_BY_IDS, insert, editor);
        orderHistoryRepository.recordDispatch(id, DispatchEvent.DISPATCH_CREATED, editor);
        return Map.of("data", id, "count", 1);
    }

    private Object executeMove(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        int assignmentId = ((Number) params.get("dispatchAssignmentId")).intValue();
        int version = ((Number) params.get("version")).intValue();
        int targetCrewId = ((Number) params.get("dailyCrewId")).intValue();
        String editor = (String) params.getOrDefault("editor", "system");
        // ① 現在の移動元を取得
        Map<String, Object> assignment = findAssignment(assignmentId);
        int sourceCrewId = ((Number) assignment.get("dailyCrewId")).intValue();
        // ② 移動元と移動先の班をID昇順でロック
        int firstCrewId = Math.min(sourceCrewId, targetCrewId);
        int secondCrewId = Math.max(sourceCrewId, targetCrewId);
        lockCrew(firstCrewId);
        if (firstCrewId != secondCrewId) {
            lockCrew(secondCrewId);
        }
        // ③ ロック取得後、最新情報を再確認
        Map<String, Object> current = findAssignment(assignmentId);
        int currentCrewId = ((Number) current.get("dailyCrewId")).intValue();
        int currentVersion = ((Number) current.get("version")).intValue();
        if (currentCrewId != sourceCrewId || currentVersion != version) {
            throw new BusinessException(
                "他の担当者によって配車情報が変更されています。"
                + "再読み込みしてください。"
            );
        }
        int visitOrder = getNextVisitOrder(targetCrewId);
        // ⑤ version付きUPDATE
        Map<String, Object> update = new HashMap<>();
        update.put("dispatchAssignmentId", assignmentId);
        update.put("version", version);
        update.put("dailyCrewId", targetCrewId);
        update.put("visitOrder", visitOrder);
        int count = baseRepository.update(Tables.DISPATCH_ASSIGNMENT_BY_IDS, update, editor);
        orderHistoryRepository.recordDispatch(assignmentId, DispatchEvent.DISPATCH_MOVED, editor);
        return Map.of("data", assignmentId, "count", count);
    }

    private void lockCrew(int dailyCrewId) {
        QueryDefinition query = QueryDefinition.select(
            """
            SELECT daily_crew_id
            FROM daily_crews WITH (UPDLOCK, HOLDLOCK)
            WHERE daily_crew_id = ?
            AND state = ?
            """,
            List.of("dailyCrewId", "state")
        );
        List<Map<String, Object>> rows = queryExecutor.select(
            query,
            selectParams(Map.of("dailyCrewId", dailyCrewId, "state", State.INITIAL.getCode()))
        );
        if (rows.isEmpty()) {
            throw new BusinessException("対象の班が存在しません。再読み込みしてください。");
        }
    }

    private Map<String, Object> findAssignment(int assignmentId) {
        SelectRequest req = new SelectRequest();
        req.setParams(Map.of("dispatchAssignmentId", assignmentId, "state", State.INITIAL.getCode()));
        QueryDefinition query = QueryDefinition.select(
            """
            SELECT
                dispatch_assignment_id,
                daily_crew_id,
                version
            FROM dispatch_assignments
            WHERE dispatch_assignment_id = ?
            AND state = ?
            """,
            List.of("dispatchAssignmentId", "state")
        );
        List<Map<String, Object>> rows = queryExecutor.select(query, req);
        if (rows.isEmpty()) {
            throw new BusinessException("配車情報が存在しません。再読み込みしてください。");
        }
        return rows.get(0);
    }

    @SuppressWarnings("unchecked")
    private Object executeAssignmentReorder(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        int dailyCrewId = ((Number) params.get("dailyCrewId")).intValue();
        String editor = (String) params.getOrDefault("editor", "system");
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        if (items == null || items.isEmpty()) {throw new BusinessException("並び替え対象がありません。");}
        // ① 対象班をロック
        lockCrew(dailyCrewId);
        // ② DBに登録されている配車割当を取得
        SelectRequest listReq = new SelectRequest();
        listReq.setParams(Map.of("dailyCrewId", dailyCrewId, "state", State.INITIAL.getCode()));
        QueryDefinition listQuery = QueryDefinition.select(
            """
            SELECT dispatch_assignment_id
            FROM dispatch_assignments
            WHERE daily_crew_id = ?
            AND state = ?
            """,
            List.of("dailyCrewId", "state")
        );
        List<Map<String, Object>> current = queryExecutor.select(listQuery, listReq);
        // ③ DB上の割当IDを取得
        Set<Integer> currentIds = new HashSet<>();
        for (Map<String, Object> row : current) {
            currentIds.add(((Number) row.get("dispatchAssignmentId")).intValue());
        }
        // ④ 画面から送信された割当IDを取得
        Set<Integer> requestedIds = new HashSet<>();
        for (Map<String, Object> item : items) {
            int id = ((Number) item.get("dispatchAssignmentId")).intValue();
            if (!requestedIds.add(id)) {
                throw new BusinessException("配車情報に重複があります。再読み込みしてください。");
            }
        }
        // ⑤ 送信された割当とDB上の割当が一致するか確認
        if (!currentIds.equals(requestedIds)) {
            throw new BusinessException(
                "他の担当者によって配車内容が変更されています。" +
                "再読み込みしてから操作してください。"
            );
        }
        // ⑥ 既存のversion付きUPDATEで並び替え
        int count = 0;
        for (Map<String, Object> item : items) {
            Map<String, Object> row = new HashMap<>();
            row.put("dispatchAssignmentId", ((Number) item.get("dispatchAssignmentId")).intValue());
            row.put("visitOrder", ((Number) item.get("visitOrder")).intValue());
            row.put("version", ((Number) item.get("version")).intValue());
            baseRepository.update(Tables.DISPATCH_ASSIGNMENT_BY_IDS, row, editor);
            orderHistoryRepository.recordDispatch(((Number) row.get("dispatchAssignmentId")).longValue(), DispatchEvent.DISPATCH_REORDERED, editor);
            count++;
        }
        return Map.of("count", count);
    }

    private Object executeReset(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        String workDate = String.valueOf(params.get("workDate"));
        int officeId = ((Number) params.get("officeId")).intValue();
        int dispatchCategory = ((Number) params.get("dispatchCategory")).intValue();
        // ① 対象となる班を取得
        List<Map<String, Object>> crews = findResetCrews(workDate, officeId, dispatchCategory);
        // ② 班IDの昇順でロック
        for (Map<String, Object> crew : crews) {
            int dailyCrewId = ((Number) crew.get("dailyCrewId")).intValue();
            lockCrew(dailyCrewId);
        }
        // 対象の配車割当を取得
        Map<String, Object> findParams = new HashMap<>();
        findParams.put("state", State.INITIAL.getCode());
        findParams.put("workDate", workDate);
        findParams.put("officeId", officeId);
        findParams.put("dispatchCategory", dispatchCategory);
        SelectRequest findReq = new SelectRequest();
        findReq.setParams(findParams);
        List<Map<String, Object>> rows = queryExecutor.select(DispatchAssignmentQuery.dispatchAssignmentResetList(), findReq);
        // 論理削除
        int count = 0;
        for (Map<String, Object> row : rows) {
            Map<String, Object> update = new HashMap<>();
            update.put("dispatchAssignmentId", row.get("dispatchAssignmentId"));
            update.put("version", row.get("version"));
            update.put("state", State.DELETE.getCode());
            baseRepository.update(Tables.DISPATCH_ASSIGNMENT_BY_IDS, update, editor);
            orderHistoryRepository.recordDispatch(((Number) row.get("dispatchAssignmentId")).longValue(), DispatchEvent.DISPATCH_RESET, editor);
            count++;
        }
        return Map.of("count", count);
    }

    private List<Map<String, Object>> findResetCrews(String workDate, int officeId, int dispatchCategory) {
        QueryDefinition query = QueryDefinition.select(
            """
            SELECT daily_crew_id
            FROM daily_crews
            WHERE work_date = ?
            AND office_id = ?
            AND dispatch_category = ?
            AND state = ?
            ORDER BY daily_crew_id
            """,
            List.of(
                "workDate",
                "officeId",
                "dispatchCategory",
                "state"
            )
        );
        return queryExecutor.select(
            query,
            selectParams(Map.of(
                "workDate", workDate,
                "officeId", officeId,
                "dispatchCategory", dispatchCategory,
                "state", State.INITIAL.getCode()
            ))
        );
    }

    private Object executeRemove(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        int assignmentId = ((Number) params.get("dispatchAssignmentId")).intValue();
        int version = ((Number) params.get("version")).intValue();
        String editor = (String) params.getOrDefault("editor", "system");
        // ① 現在の所属班を取得
        Map<String, Object> assignment = findAssignment(assignmentId);
        int dailyCrewId = ((Number) assignment.get("dailyCrewId")).intValue();
        // ② 所属班をロック
        lockCrew(dailyCrewId);
        // ③ ロック取得後に最新状態を確認
        Map<String, Object> current = findAssignment(assignmentId);
        int currentCrewId = ((Number) current.get("dailyCrewId")).intValue();
        int currentVersion = ((Number) current.get("version")).intValue();
        if (currentCrewId != dailyCrewId || currentVersion != version) {
            throw new BusinessException(
                "他の担当者によって配車情報が変更されています。"
                + "再読み込みしてください。"
            );
        }
        // ④ version付き論理削除
        Map<String, Object> update = new HashMap<>();
        update.put("dispatchAssignmentId", assignmentId);
        update.put("version", version);
        update.put("state", State.DELETE.getCode());
        int count = baseRepository.update(Tables.DISPATCH_ASSIGNMENT_BY_IDS, update, editor);
        orderHistoryRepository.recordDispatch(assignmentId, DispatchEvent.DISPATCH_REMOVED, editor);
        return Map.of("count", count);
    }

    private int getNextVisitOrder(int dailyCrewId) {
        List<Map<String, Object>> rows = queryExecutor.select(
            DispatchAssignmentQuery.dispatchAssignmentNextVisitOrder(),
            selectParams(Map.of("dailyCrewId", dailyCrewId, "state", State.INITIAL.getCode()))
        );
        return ((Number) rows.get(0).get("nextVisitOrder")).intValue();
    }

    private SelectRequest selectParams(Map<String, Object> params) {
        SelectRequest req = new SelectRequest();
        req.setParams(params);
        return req;
    }
}