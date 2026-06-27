package com.kyouseipro.neo.domain.personnel.employee.workskill;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.interfaces.sql.QueryBuilderPart;
import com.kyouseipro.neo.sql.query.personnel.workskill.WorkSkillQuery;

@Component

public class WorkSkillQueryBuilderProvider implements QueryBuilderPart {

    @Override
    public Map<QueryId, QueryBuilder> provide() {
        Map<QueryId, QueryBuilder> map = new HashMap<>();
        map.put(QueryId.WORK_SKILL_LIST, req -> WorkSkillQuery.workSkillList());
        map.put(QueryId.WORK_SKILL_DETAIL, req -> WorkSkillQuery.workSkillDetail());
        return map;
    }
}