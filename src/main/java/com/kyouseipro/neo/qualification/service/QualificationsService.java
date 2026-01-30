package com.kyouseipro.neo.qualification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.qualification.entity.QualificationsEntity;
import com.kyouseipro.neo.qualification.repository.QualificationsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QualificationsService {
    private final QualificationsRepository qualificationsRepository;

    /**
     * 指定されたIDの資格情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 資格ID
     * @return QualificationsEntity または null
     */
    public List<QualificationsEntity> getByEmployeeId(Integer id) {
        return qualificationsRepository.findAllByEmployeeId(id);
    }

    /**
     * 指定されたIDの許認可情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 資格ID
     * @return QualificationsEntity または null
     */
    public List<QualificationsEntity> getByCompanyId(Integer id) {
        return qualificationsRepository.findAllByCompanyId(id);
    }

    /**
     * 資格情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    @HistoryTarget(
        table = HistoryTables.QUALIFICATIONS,
        action = "保存"
    )
    public int save(QualificationsEntity entity, String editor) {
        if (entity.getQualificationsId() > 0) {
            return qualificationsRepository.update(entity, editor);
        } else {
            return qualificationsRepository.insert(entity, editor);
        }
    }

    /**
     * IDからQualificationsを削除
     * @param id
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.QUALIFICATIONS,
        action = "削除"
    )
    public int deleteById(int id, String userName) {
        return qualificationsRepository.delete(id, userName);
    }

    /**
     * IDSからQualificationsを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.QUALIFICATIONS,
        action = "削除"
    )
    public int deleteByIds(List<SimpleData> list, String userName) {
        return qualificationsRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list, String userName) {
        List<QualificationsEntity> qualifications = qualificationsRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(qualifications, QualificationsEntity.class);
    }

    /**
     * すべての資格情報を取得
     * @return
     */
    public List<QualificationsEntity> getListForEmployee() {
        return qualificationsRepository.findAllByEmployeeStatus();
    }

    /**
     * すべての許認可情報を取得
     * @return
     */
    public List<QualificationsEntity> getListFroCompany() {
        return qualificationsRepository.findAllByCompanyStatus();
    }
}
