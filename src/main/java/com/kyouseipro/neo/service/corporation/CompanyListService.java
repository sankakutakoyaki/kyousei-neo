package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.repository.corporation.CompanyListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyListService {
    private final CompanyListRepository companyListRepository;

    
    /**
     * すべてのClientを取得
     * @return
     */
    public List<CompanyListEntity> getClientList() {
        return companyListRepository.findAllClient();
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0);");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべてのCompanyを取得
     * @return
     */
    public List<CompanyListEntity> getCompanyList() {
        return companyListRepository.findAll();
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のCompanyを取得
     * @return
     */
    public List<CompanyListEntity> getCompanyListByCategory(int category) {
        return companyListRepository.findByCategoryId(category);
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }
}
