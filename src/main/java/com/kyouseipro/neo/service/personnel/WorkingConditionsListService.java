package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.personnel.WorkingConditionsListEntity;
import com.kyouseipro.neo.repository.personnel.WorkingConditionsListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkingConditionsListService {
    private final WorkingConditionsListRepository workingConditionsListRepository;

    /**
     * すべてのWorkingConditionsを取得
     * @return
     */
    public List<WorkingConditionsListEntity> getWorkingConditionsList() {
        return workingConditionsListRepository.findAll();
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0);");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のWorkingConditionsを取得
     * @return
     */
    public List<WorkingConditionsListEntity> getWorkingConditionsListByCategory(int category) {
        return workingConditionsListRepository.findByCategoryId(category);
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }    
}
