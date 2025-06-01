package com.kyouseipro.neo.service.company;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.repository.CompanyRepository;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;

    /**
     * 指定されたIDの会社情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 会社ID
     * @return CompanyEntity または null
     */
    public CompanyEntity getCompanyById(int id) {
        return companyRepository.findById(id);
    }

    /**
     * 会社情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    public Integer saveCompany(CompanyEntity entity, String editor) {
        if (entity.getCompany_id() > 0) {
            return companyRepository.updateCompany(entity, editor);
        } else {
            return companyRepository.insertCompany(entity, editor);
        }
    }

    // /**
    //  * IDからCompanyを取得
    //  * @param account
    //  * @return
    //  */
    // public Entity getCompanyById(int id) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT * FROM companies WHERE company_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new CompanyEntity());
    //     return sqlRepository.getEntity(sqlData);
    // }

    // /**
    //  * すべてのCompanyを取得
    //  * @return
    //  */
    // public List<Entity> getCompanyList() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(CompanyListEntity.selectString());
    //     sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new CompanyListEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    /**
     * すべてのClientを取得
     * @return
     */
    public List<CompanyEntity> getClientList() {
        return companyRepository.findAllClient();
        // StringBuilder sb = new StringBuilder();
        // sb.append(CompanyListEntity.selectString());
        // sb.append(" WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0);");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyListEntity());
        // return sqlRepository.getEntityList(sqlData);
    }

    // /**
    //  * カテゴリー別のCompanyを取得
    //  * @return
    //  */
    // public List<Entity> getCompanyListByCategory(int category) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(CompanyListEntity.selectString());
    //     sb.append(" WHERE category = " + category + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new CompanyListEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * Companyを保存
    //  * @param company
    //  * @return
    //  */
    // public Entity saveCompany(CompanyEntity entity) {
    //     StringBuilder sb = new StringBuilder();
    //     if (entity.getCompany_id() > 0) {
    //         sb.append(entity.getUpdateString());
    //     } else {
    //         sb.append(entity.getInsertString());
    //     }
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからCompanyeを削除
    //  * @param ids
    //  * @return
    //  */
    // public Entity deleteCompanyByIds(List<SimpleData> list, String userName) {
    //     String ids = Utilities.createSequenceByIds(list);
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("UPDATE companies SET state = " + Enums.state.DELETE.getNum() + " WHERE company_id IN(" + ids + ");");
    //     sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
    //     sb.append("IF @ROW_COUNT > 0 BEGIN ");
    //     sb.append(HistoryEntity.insertString(userName, "companies", "削除成功", "@ROW_COUNT", ""));
    //     sb.append("SELECT 200 as number, '削除しました' as text; END");
    //     sb.append(" ELSE BEGIN ");
    //     sb.append(HistoryEntity.insertString(userName, "companies", "削除失敗", "@ROW_COUNT", ""));
    //     sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからCsv用文字列を取得
    //  * @param ids
    //  * @return
    //  */
    // public String downloadCsvCompanyByIds(List<SimpleData> list, String userName) {
    //     String ids = Utilities.createSequenceByIds(list);
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT * FROM companies WHERE company_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new CompanyEntity());
    //     List<Entity> entities = sqlRepository.getEntityList(sqlData);
    //     return CompanyEntity.getCsvString(entities);
    // }
}
