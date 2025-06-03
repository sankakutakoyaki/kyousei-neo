package com.kyouseipro.neo.repository.personnel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.mapper.personnel.EmployeeEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.EmployeeParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.EmployeeSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {
    private final SqlRepository sqlRepository;

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


    public int deleteEmployeeByIds(List<SimpleData> ids, String editor) {
        List<Integer> employeeIds = Utilities.createSequenceByIds(ids);
        String sql = EmployeeSqlBuilder.buildDeleteEmployeeForIdsSql(employeeIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> EmployeeParameterBinder.bindDeleteForIds(ps, employeeIds)
        );

        return result; // 成功件数。0なら削除なし
    }

    public List<EmployeeEntity> downloadCsvEmployeeByIds(List<SimpleData> ids, String editor) {
        List<Integer> employeeIds = Utilities.createSequenceByIds(ids);
        String sql = EmployeeSqlBuilder.buildDownloadCsvCompanyForIdsSql(employeeIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> EmployeeParameterBinder.bindFindAll(ps, null),
            EmployeeEntityMapper::map // ← ここで ResultSet を map
        );
    }
   
    // IDによる取得
    public EmployeeEntity findById(int employeeId) {
        String sql = EmployeeSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> EmployeeParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            employeeId
        );
    }

    // アカウントによる取得
    public EmployeeEntity findByAccount(String account) {
        String sql = EmployeeSqlBuilder.buildFindByAccountSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> EmployeeParameterBinder.bindFindByAccount(pstmt, comp),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            account
        );
    }

    // 全件取得
    public List<EmployeeEntity> findAll() {
        String sql = EmployeeSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> EmployeeParameterBinder.bindFindAll(ps, null),
            EmployeeEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
