package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.service.QueryExecutor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
    private final SqlProvider sqlProvider;
    private final QueryExecutor queryExecutor;
    private final SqlRepository sqlRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.EMPLOYEE_SAVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch(def.getKind()) {
            case EMPLOYEE_SAVE -> executeSave(req);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private Object executeSave(SelectRequest req) {
        Map<String,Object> source = req.getParams();
        String editor = (String)source.getOrDefault("editor", "system");
        List<Integer> workCategoryIds = (List<Integer>)source.get("workCategoryIds");

        // employees用payload
        Map<String,Object> employee = new HashMap<>(source);
        employee.remove("workCategoryIds");
        Object idValue = employee.get("employeeId");

        Long employeeId;
        if(idValue == null || number(idValue) == 0){
            employeeId = baseRepository.insert(Tables.EMPLOYEE_BY_IDS, employee, editor);
        }else{
            baseRepository.update(Tables.EMPLOYEE_BY_IDS, employee, editor);
            employeeId = ((Number)idValue).longValue();
        }
        // 次に業務区分を保存
        saveWorkCategories(employeeId, workCategoryIds, editor);
        return Map.of("id", employeeId);
    }

    private void saveWorkCategories(Long employeeId, List<Integer> workCategoryIds, String editor){
        List<Integer> selectedIds = workCategoryIds != null ? workCategoryIds: List.of();
        QueryDefinition def = sqlProvider.get(QueryId.EMPLOYEE_WORK_CATEGORY_MEMBER_LIST);

        SelectRequest selectReq = new SelectRequest();
        selectReq.setParams(Map.of("employeeId", employeeId));
        List<Map<String,Object>> currentRows = queryExecutor.select(def, selectReq);
        Map<Integer,Map<String,Object>> currentMap = new HashMap<>();

        for(Map<String,Object> row : currentRows){
            int categoryId = number(row.get("employeeWorkCategoryId"));
            currentMap.put(categoryId, row);
        }
        // 既存レコードの更新
        for(Map<String,Object> row : currentRows){
            int categoryId = number(row.get("employeeWorkCategoryId"));
            int state = number(row.get("state"));
            boolean selected = selectedIds.contains(categoryId);
            // 選択されたまま
            if(selected && state == 0){
                continue;
            }
            // 外されたまま
            if(!selected && state != 0){
                continue;
            }
            // 削除済みを再有効化
            if(selected && state == 9){
                sqlRepository.updateRequired(
                    """
                    UPDATE employee_work_category_members
                    SET
                        state = 0,
                        version = version + 1,
                        update_date = SYSDATETIME()
                    WHERE employee_work_category_member_id = ?
                    AND version = ?
                    AND state = 9
                    """,
                    List.of(
                        row.get(
                            "employeeWorkCategoryMemberId"
                        ),
                        row.get(
                            "version"
                        )
                    ),
                    "業務区分の再登録に失敗しました"
                );
                continue;
            }
            // 有効なものを外す
            Map<String,Object> update = new HashMap<>();
            update.put("employeeWorkCategoryMemberId", row.get("employeeWorkCategoryMemberId"));
            update.put("version", row.get("version"));
            update.put("state", 9);
            baseRepository.update(Tables.EMPLOYEE_WORK_CATEGORY_MEMBER_BY_IDS, update, editor);
        }
        // 新規追加
        for(Integer categoryId : selectedIds){
            if(currentMap.containsKey(categoryId)){
                continue;
            }
            Map<String,Object> insert = new HashMap<>();
            insert.put("employeeId", employeeId);
            insert.put("employeeWorkCategoryId", categoryId);
            insert.put("state", 0);
            baseRepository.insert(Tables.EMPLOYEE_WORK_CATEGORY_MEMBER_BY_IDS, insert, editor);
        }
    }

    private int number(Object value) {
        if(value == null){
            return 0;
        }
        if(value instanceof Number number){
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}