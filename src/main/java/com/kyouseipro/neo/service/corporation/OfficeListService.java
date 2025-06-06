package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.repository.corporation.OfficeListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeListService {
    private final OfficeListRepository officeListRepository;

    /**
     * すべてのOfficeを取得
     * @return
     */
    public List<OfficeListEntity> getOfficeList() {
        return officeListRepository.findAll();
        // StringBuilder sb = new StringBuilder();
        // sb.append(OfficeListEntity.selectString());
        // sb.append(" WHERE NOT (o.state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new OfficeListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべてのClientOfficeを取得
     * @return
     */
    public List<OfficeListEntity> getClientList() {
        return officeListRepository.findByCategoryId(0);
        // StringBuilder sb = new StringBuilder();
        // sb.append(OfficeListEntity.selectString());
        // sb.append(" WHERE NOT (o.state = " + Enums.state.DELETE.getNum() + ") AND NOT (c.category = 0);");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new OfficeListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のOfficeを取得
     * @return
     */
    public List<OfficeListEntity> getOfficeListByCategory(int category) {
        return officeListRepository.findByCategoryId(category);
        // StringBuilder sb = new StringBuilder();
        // sb.append(OfficeListEntity.selectString());
        // sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new OfficeListEntity());
        // return sqlRepository.getEntityList(sqlData);
    } 
}
