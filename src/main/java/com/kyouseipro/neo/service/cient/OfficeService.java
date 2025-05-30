package com.kyouseipro.neo.service.cient;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final SqlRepository sqlRepository;
    /**
     * IDからOfficeを取得
     * @param account
     * @return
     */
    public Entity getOfficeById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT o.*, c.name as company_name FROM offices o");
        sb.append(" INNER JOIN companies c ON c.company_id = o.company_id AND NOT (c.state = "  + Enums.state.DELETE.getNum() + ")");
        sb.append(" WHERE o.office_id = " + id + " AND NOT (o.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * すべてのOfficeを取得
     * @return
     */
    public List<Entity> getOfficeList() {
        StringBuilder sb = new StringBuilder();
        sb.append(OfficeListEntity.selectString());
        sb.append(" WHERE NOT (o.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべてのClientOfficeを取得
     * @return
     */
    public List<Entity> getClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append(OfficeListEntity.selectString());
        sb.append(" WHERE NOT (o.state = " + Enums.state.DELETE.getNum() + ") AND NOT (c.category = 0);");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のOfficeを取得
     * @return
     */
    public List<Entity> getOfficeListByCategory(int category) {
        StringBuilder sb = new StringBuilder();
        sb.append(OfficeListEntity.selectString());
        sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * Officeを保存
     * @param company
     * @return
     */
    public Entity saveOffice(OfficeEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getOffice_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからOfficeeを削除
     * @param ids
     * @return
     */
    public Entity deleteOfficeByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE offices SET state = " + Enums.state.DELETE.getNum() + " WHERE company_id IN(" + ids + ");");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        sb.append("IF @ROW_COUNT > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "offices", "削除成功", "@ROW_COUNT", ""));
        sb.append("SELECT 200 as number, '削除しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "offices", "削除失敗", "@ROW_COUNT", ""));
        sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvOfficeByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM offices WHERE office_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeEntity());
        List<Entity> entities = sqlRepository.getEntityList(sqlData);
        return OfficeEntity.getCsvString(entities);
    }
}

