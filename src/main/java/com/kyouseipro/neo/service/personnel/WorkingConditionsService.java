package com.kyouseipro.neo.service.personnel;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
import com.kyouseipro.neo.repository.personnel.WorkingConditionsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkingConditionsService {
    private final WorkingConditionsRepository workingConditionsRepository;

    /**
     * 指定されたIDの労働条件情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 労働条件ID
     * @return WorkingConditionsEntity または null
     */
    public Optional<WorkingConditionsEntity> getById(int id) {
        return workingConditionsRepository.findById(id);
    }

    /**
     * EmployeeIDで指定された従業員の労働条件情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return WorkingConditionsEntity または null
     */
    public Optional<WorkingConditionsEntity> getByEmployeeId(int id) {
        return workingConditionsRepository.findByEmployeeId(id);
    }

    /**
     * 労働条件情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    @HistoryTarget(
        table = HistoryTables.WORKINGCONDITIONS,
        action = "保存"
    )
    public int save(WorkingConditionsEntity entity, String editor) {
        if (entity.getWorking_conditions_id() > 0) {
            return workingConditionsRepository.update(entity, editor);
        } else {
            return workingConditionsRepository.insert(entity, editor);
        }
    }

    /**
     * IDからWorkingConditionsを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.WORKINGCONDITIONS,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return workingConditionsRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<WorkingConditionsEntity> workingConditions = workingConditionsRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(workingConditions, WorkingConditionsEntity.class);
    }
}
