package com.kyouseipro.neo.sql.provider;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.enums.code.RecycleCategory;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.interfaces.sql.QueryBuilder;
import com.kyouseipro.neo.sql.query.recycle.RecycleMakerQuery;
import com.kyouseipro.neo.sql.query.recycle.RecycleQuery;

@Component
public class QueryBuilderProvider {
    private final Map<QueryId, QueryBuilder> builders = new HashMap<>();

    public QueryBuilderProvider() {
        // builders.put(QueryId.RECYCLE_LIST, req -> {
        //     String categoryStr = (String) req.getParams().get("category");
        //     RecycleCategory category =
        //         EnumUtil.of(RecycleCategory.class, Integer.parseInt(categoryStr));
        //     return RecycleQuery.recycleList(category);
        // });
        builders.put(QueryId.RECYCLE_LIST, req -> {
            Integer categoryInt = toInteger(req.getParams().get("category"));
            RecycleCategory category = categoryInt == null ? null: EnumUtil.of(RecycleCategory.class, categoryInt);
            return RecycleQuery.recycleList(category);
        });

        builders.put(QueryId.RECYCLE_MAKER_LIST, req -> {
            return RecycleMakerQuery.recycleMakerList();
        });


        // ここに追加

    }

    public QueryBuilder get(QueryId id) {
        return builders.get(id);
    }

    private Integer toInteger(Object value){
        if(value == null) return null;

        String s = value.toString();
        if(s.isBlank()) return null;

        return Integer.parseInt(s);
    }
}