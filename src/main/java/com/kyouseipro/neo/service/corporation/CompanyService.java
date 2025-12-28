package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
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
    public CompanyEntity getById(int id) {
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
    public Integer save(CompanyEntity entity, String editor) {
        if (entity.getCompany_id() > 0) {
            return companyRepository.update(entity, editor);
        } else {
            return companyRepository.insert(entity, editor);
        }
    }

    /**
     * IDからCompanyeを削除
     * @param ids
     * @return
     */
    public Integer deleteByIds(List<SimpleData> list, String userName) {
        return companyRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list, String userName) {
        List<CompanyEntity> companies = companyRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(companies, CompanyEntity.class);
    }
}
