package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.csv.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.repository.personnel.EmployeeRepository;

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
    public EmployeeEntity getEmployeeById(int id) {
        return employeeRepository.findById(id);
    }

    /**
     * 従業員情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveEmployee(EmployeeEntity entity, String editor) {
        if (entity.getEmployee_id() > 0) {
            return employeeRepository.updateEmployee(entity, editor);
        } else {
            return employeeRepository.insertEmployee(entity, editor);
        }
    }

    /**
     * アカウントからEmployeeを取得
     * @param account
     * @return
     */
    public EmployeeEntity getEmployeeByAccount(String account) {
        return employeeRepository.findByAccount(account);
    }

    /**
     * IDからEmployeeを削除
     * @param ids
     * @return
     */
    public Integer deleteEmployeeByIds(List<SimpleData> list, String userName) {
        return employeeRepository.deleteEmployeeByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvEmployeeByIds(List<SimpleData> list, String userName) {
        List<EmployeeEntity> employees = employeeRepository.downloadCsvEmployeeByIds(list, userName);
        return CsvExporter.export(employees, EmployeeEntity.class);
    }
}
