package com.kyouseipro.neo.sql.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.enums.system.QueryKind;
import com.kyouseipro.neo.interfaces.sql.QueryHandler;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeShiftHandler implements QueryHandler {

    private final BaseSqlRepository baseRepository;

    @Override
    public boolean supports(QueryKind kind) {
        return kind == QueryKind.EMPLOYEE_SHIFT_BATCH_SAVE;
    }

    @Override
    @Transactional
    public Object execute(QueryDefinition def, SelectRequest req) {
        return switch(def.getKind()) {
            case EMPLOYEE_SHIFT_BATCH_SAVE -> executeBatchSave(req);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private Object executeBatchSave(SelectRequest req) {
        Map<String, Object> params = req.getParams();
        String editor = (String) params.getOrDefault("editor", "system");
        Object value = params.get("rows");
        if(!(value instanceof List<?>)){
            throw new IllegalArgumentException("rowsが指定されていません。");
        }
        List<Map<String, Object>> rows = (List<Map<String, Object>>) value;
        int count = 0;
        for(Map<String, Object> source : rows){
            saveRow(source, editor);
            count++;
        }
        return Map.of("count", count);
    }

    private void saveRow(Map<String, Object> source, String editor) {
        Object idValue = source.get("employeeShiftId");
        // 新規
        if(idValue == null){
            // 未登録の -- は保存しない
            if(number(source.get("shiftType")) == 0){
                return;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("employeeId", source.get("employeeId"));
            row.put("workDate", source.get("workDate"));
            row.put("officeId", source.get("officeId"));
            row.put("shiftType", source.get("shiftType"));
            row.put("state", 0);
            baseRepository.insert(Tables.EMPLOYEE_SHIFT_BY_IDS, row, editor);

            return;
        }
        // 更新・論理削除
        Map<String, Object> row = new HashMap<>();
        row.put("employeeShiftId", idValue);
        row.put("version", source.get("version"));
        row.put("shiftType", source.get("shiftType"));
        row.put("state", source.get("state"));
        baseRepository.update(Tables.EMPLOYEE_SHIFT_BY_IDS, row, editor);
    }


    private int number(Object value) {
        if(value instanceof Number number){
            return number.intValue();
        }
        return Integer.parseInt(
            String.valueOf(value)
        );
    }
}