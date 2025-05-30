package com.kyouseipro.neo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableService {
    private final SqlRepository sqlRepository;

    /**
     * IDからEntityを取得
     * @param account
     * @return
     */
    public Entity getEntityById(int id, Entity entity) {
        StringBuilder sb = new StringBuilder();
        SqlData sqlData = new SqlData();
        EntityQueryBuilder builder = QueryBuilderFactory.getBuilder(entity);
        SqlData data = builder.buildSelectById(id, entity);
        sqlData.setData(sb.toString(), entity);
        return sqlRepository.getEntity(data);
    }

        // if (entity instanceof CompanyEntity) {
        //     String sql = "SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?);";
        //     sqlData.setData(sql, entity);
        //     sqlData.setParameters(List.of(id, Enums.state.DELETE.getNum()));
        // } else {
        //     throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass());
        // }
        // sqlData.setData(sb.toString(), entity);
        // return sqlRepository.getEntity(sqlData);
    }

    /**
     * すべてのEntityを取得
     * @return
     */
    public List<Entity> getEntityList(String className) {
        StringBuilder sb = new StringBuilder();
        SqlData sqlData = new SqlData();
        switch (className) {
            case "companies":
                sb.append(CompanyListEntity.selectString());
                sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getCode() + ");");
                sqlData.setData(sb.toString(), new CompanyListEntity());
                break;
        
            default:
                break;
        }

        return sqlRepository.getEntityList(sqlData);
    }
}
