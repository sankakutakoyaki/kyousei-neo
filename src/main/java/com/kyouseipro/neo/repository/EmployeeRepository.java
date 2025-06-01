package com.kyouseipro.neo.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.query.parameter.EmployeeParameterBinder;
import com.kyouseipro.neo.query.sql.EmployeeSqlBuilder;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public Integer insertEmployee(EmployeeEntity employee, String editor) {
        String sql = EmployeeSqlBuilder.buildInsertEmployeeSql();

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> EmployeeParameterBinder.bindInsertEmployeeParameters(pstmt, emp, editor),
            rs -> rs.next() ? rs.getInt("employee_id") : null,
            employee
        );
    }

    public Integer updateEmployee(EmployeeEntity employee, String editor) {
        String sql = EmployeeSqlBuilder.buildUpdateEmployeeSql();

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> EmployeeParameterBinder.bindUpdateEmployeeParameters(pstmt, emp, editor),
            rs -> rs.next() ? rs.getInt("employee_id") : null,
            employee
        );
    }

    // employe_idによる取得
    public EmployeeEntity findById(int employeeId) {
        return genericRepository.findOne(
        "SELECT * FROM employees WHERE employee_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, employeeId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            EmployeeEntity::new // Supplier<T>
        );
    }

    public EmployeeEntity findByAccount(String account) {
        return genericRepository.findOne(
        "SELECT * FROM employees WHERE account = ? AND NOT (state = ?)",
            ps -> {
                ps.setString(1, account); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            EmployeeEntity::new // Supplier<T>
        );
    }

    // 全件取得の例（必要に応じて）
    public List<EmployeeEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM employees WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            EmployeeEntity::new
        );
    }
}
