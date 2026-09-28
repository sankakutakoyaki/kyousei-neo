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
import com.kyouseipro.neo.sql.service.QueryExecutor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;
    private final SqlProvider sqlProvider;
    private final QueryExecutor queryExecutor;

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
        List<Integer> employeeOfficeIds = (List<Integer>)source.get("employeeOfficeIds");

        // employees用payload
        Map<String,Object> employee = new HashMap<>(source);
        employee.remove("workCategoryIds");
        employee.remove("employeeOfficeIds");
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
        saveOfficeMembers(employeeId, employeeOfficeIds, editor);
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
            if(!selected && state == 9){
                continue;
            }
            // 削除済みを再有効化
            if(selected && state == 9){
                baseRepository.reactivate(Tables.EMPLOYEE_WORK_CATEGORY_MEMBER_BY_IDS, row.get("employeeWorkCategoryMemberId"), row.get("version"), editor);
                continue;
            }
            // 有効なものを外す
            if(!selected && state == 0){
                Map<String,Object> update = new HashMap<>();
                update.put("employeeWorkCategoryMemberId", row.get("employeeWorkCategoryMemberId"));
                update.put("version", row.get("version"));
                update.put("state", 9);
                baseRepository.update(Tables.EMPLOYEE_WORK_CATEGORY_MEMBER_BY_IDS, update, editor);
                continue;
            }
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

    private void saveOfficeMembers(Long employeeId, List<Integer> employeeOfficeIds, String editor){
        List<Integer> selectedIds = employeeOfficeIds != null ? employeeOfficeIds: List.of();
        QueryDefinition def = sqlProvider.get(QueryId.EMPLOYEE_OFFICE_MEMBER_LIST);
        SelectRequest selectReq = new SelectRequest();
        selectReq.setParams(Map.of("employeeId", employeeId));
        List<Map<String,Object>> currentRows = queryExecutor.select(def, selectReq);
        Map<Integer,Map<String,Object>> currentMap = new HashMap<>();
        for(Map<String,Object> row : currentRows){
            int officeId = number(row.get("officeId"));
            currentMap.put(officeId, row);
        }
        /*
        * 既存行
        */
        for(Map<String,Object> row : currentRows){
            int officeId = number(row.get("officeId"));
            int state = number(row.get("state"));
            boolean selected = selectedIds.contains(officeId);
            // 選択されたまま
            if(selected && state == 0){
                continue;
            }
            // 外されたまま
            if(!selected && state == 9){
                continue;
            }
            // 削除済みを再有効化
            if(selected && state == 9){
                baseRepository.reactivate(Tables.EMPLOYEE_OFFICE_MEMBER_BY_IDS, row.get("employeeOfficeMemberId"), row.get("version"), editor);
                continue;
            }
            // 有効なものを外す
            if(!selected && state == 0){
                Map<String,Object> update = new HashMap<>();
                update.put("employeeOfficeMemberId", row.get("employeeOfficeMemberId"));
                update.put("version", row.get("version"));
                update.put("state", 9);
                baseRepository.update(Tables.EMPLOYEE_OFFICE_MEMBER_BY_IDS, update, editor);
                continue;
            }
        }
        /*
        * 完全な新規
        */
        for(Integer officeId : selectedIds){
            if(currentMap.containsKey(officeId)){
                continue;
            }
            Map<String,Object> insert = new HashMap<>();
            insert.put("employeeId", employeeId);
            insert.put("officeId", officeId);
            insert.put("state", 0);
            baseRepository.insert(Tables.EMPLOYEE_OFFICE_MEMBER_BY_IDS, insert, editor);
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