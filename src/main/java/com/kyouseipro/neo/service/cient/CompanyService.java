package com.kyouseipro.neo.service.cient;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final SqlRepository sqlRepository;
    /**
     * IDからCompanyを取得
     * @param account
     * @return
     */
    public IEntity getCompanyById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM companies WHERE company_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new CompanyEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * すべてのCompanyを取得
     * @return
     */
    public List<IEntity> getCompanyList() {
        StringBuilder sb = new StringBuilder();
        sb.append(CompanyListEntity.selectString());
        sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new CompanyListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべてのClientを取得
     * @return
     */
    public List<IEntity> getClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append(CompanyListEntity.selectString());
        sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0);");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new CompanyListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のCompanyを取得
     * @return
     */
    public List<IEntity> getCompanyListByCategory(int category) {
        StringBuilder sb = new StringBuilder();
        sb.append(CompanyListEntity.selectString());
        sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new CompanyListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * Companyを保存
     * @param company
     * @return
     */
    public IEntity saveCompany(CompanyEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getCompany_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからCompanyeを削除
     * @param ids
     * @return
     */
    public IEntity deleteCompanyByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE companies SET state = " + Enums.state.DELETE.getNum() + " WHERE company_id IN(" + ids + ");");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        sb.append("IF @ROW_COUNT > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "companies", "削除成功", "@ROW_COUNT", ""));
        sb.append("SELECT 200 as number, '削除しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "companies", "削除失敗", "@ROW_COUNT", ""));
        sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvCompanyByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM companies WHERE company_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new CompanyEntity());
        List<IEntity> entities = sqlRepository.getEntityList(sqlData);
        return CompanyEntity.getCsvString(entities);
    }
}
