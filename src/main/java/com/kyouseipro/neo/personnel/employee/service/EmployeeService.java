package com.kyouseipro.neo.personnel.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    /**
     * 指定されたIDの従業員情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return EmployeeEntity または null
     */
    public Optional<EmployeeEntity> getById(int id) {
        return employeeRepository.findById(id);
    }

    /**
     * 従業員情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.EMPLOYEES,
        action = "保存"
    )
    public int save(EmployeeEntity entity, String editor) {
        if (entity.getEmployeeId() > 0) {
            return employeeRepository.update(entity, editor);
        } else {
            return employeeRepository.insert(entity, editor);
        }
    }

    /**
     * 従業員コードを更新します。
     * @param id
     * @param data
     * @return 成功した場合は更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.EMPLOYEES,
        action = "コード更新"
    )
    public int updateCode(int id, String code, String editor) {
        int value = 0;
        if (code != null && !code.isBlank()) {
            try {
                value = Integer.parseInt(code);
            } catch (NumberFormatException e) {
                value = 0;
            }
        }
        return employeeRepository.updateCode(id, value, editor);
    }

    /**
     * 登録携帯番号を更新します。
     * @param id
     * @param data
     * @return 成功した場合は更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.EMPLOYEES,
        action = "電話番号更新"
    )
    public int updatePhone(int id, String phone, String editor) {
        return employeeRepository.updatePhone(id, phone, editor);
    }

    /**
     * アカウントからEmployeeを取得
     * @param account
     * @return
     */
    public Optional<EmployeeEntity> getByAccount(String account) {
        return employeeRepository.findByAccount(account);
    }

    /**
     * IDからEmployeeを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.EMPLOYEES,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return employeeRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<EmployeeEntity> employees = employeeRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(employees, EmployeeEntity.class);
    }
}
