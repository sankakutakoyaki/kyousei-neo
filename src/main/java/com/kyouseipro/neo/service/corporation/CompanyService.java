package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.csv.CsvExporter;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.repository.corporation.CompanyRepository;

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

    /**
     * IDからCompanyeを削除
     * @param ids
     * @return
     */
    public Integer deleteCompanyByIds(List<SimpleData> list, String userName) {
        return companyRepository.deleteCompanyByIds(list, userName);
        // String ids = Utilities.createSequenceByIds(list);
        // StringBuilder sb = new StringBuilder();
        // sb.append("UPDATE companies SET state = " + Enums.state.DELETE.getNum() + " WHERE company_id IN(" + ids + ");");
        // sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        // sb.append("IF @ROW_COUNT > 0 BEGIN ");
        // sb.append(HistoryEntity.insertString(userName, "companies", "削除成功", "@ROW_COUNT", ""));
        // sb.append("SELECT 200 as number, '削除しました' as text; END");
        // sb.append(" ELSE BEGIN ");
        // sb.append(HistoryEntity.insertString(userName, "companies", "削除失敗", "@ROW_COUNT", ""));
        // sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
        // return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvCompanyByIds(List<SimpleData> list, String userName) {
        List<CompanyEntity> companies = companyRepository.downloadCsvCompanyByIds(list, userName);
        return CsvExporter.export(companies, CompanyEntity.class);

        // String ids = Utilities.createSequenceByIds(list);
        // StringBuilder sb = new StringBuilder();
        // sb.append("SELECT * FROM companies WHERE company_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new CompanyEntity());
        // List<Entity> entities = sqlRepository.getEntityList(sqlData);
        // return CompanyEntity.getCsvString(entities);
    }
}
