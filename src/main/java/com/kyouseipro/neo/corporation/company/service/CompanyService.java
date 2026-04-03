package com.kyouseipro.neo.corporation.company.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntity;
import com.kyouseipro.neo.corporation.company.repository.CompanyRepository;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;

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
    @HistoryTarget(
        table = HistoryTables.COMPANIES,
        action = "保存"
    )
    public int save(Map<String, Object> req, String userName) {
        Integer id = (Integer) req.get("companyId");
        if (id != null && id > 0) {
            return companyRepository.update(req, userName);
        } else {
            return companyRepository.insert(req, userName);
        }
    }

    /**
     * IDからCompanyeを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.COMPANIES,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return companyRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<CompanyEntity> companies = companyRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(companies, CompanyEntity.class);
    }
}
