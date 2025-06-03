package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.repository.personnel.EmployeeListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeListService {
    private final EmployeeListRepository employeeListRepository;

    /**
     * すべてのEmployeeを取得
     * @return
     */
    public List<EmployeeListEntity> getEmployeeList() {
        return employeeListRepository.findAll();
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0);");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のEmployeeを取得
     * @return
     */
    public List<EmployeeListEntity> getEmployeeListByCategory(int category) {
        return employeeListRepository.findByCategoryId(category);
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }
}
