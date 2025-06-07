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

    /**
     * 新規作成。
     * @param employee
     * @param editor
     * @return 新規IDを返す。
     */
    public Integer insertEmployee(EmployeeEntity employee, String editor) {
        String sql = EmployeeSqlBuilder.buildInsertEmployeeSql();

        return sqlRepository.execute(
            sql,
            (pstmt, emp) -> EmployeeParameterBinder.bindInsertEmployeeParameters(pstmt, emp, editor),
            rs -> rs.next() ? rs.getInt("employee_id") : null,
            employee
        );
    }

    /**
     * 更新。
     * @param employee
     * @param editor
     * @return 成功したか失敗したかを真偽値で返す。
     */
    public int updateEmployee(EmployeeEntity employee, String editor) {
        String sql = EmployeeSqlBuilder.buildUpdateEmployeeSql();

        int result = sqlRepository.executeUpdate(
            sql,
            pstmt -> EmployeeParameterBinder.bindUpdateEmployeeParameters(pstmt, employee, editor)
        );

        return result; // 成功件数。0なら削除なし
    }

    /**
     * 削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteEmployeeByIds(List<SimpleData> ids, String editor) {
        List<Integer> employeeIds = Utilities.createSequenceByIds(ids);
        String sql = EmployeeSqlBuilder.buildDeleteEmployeeForIdsSql(employeeIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> EmployeeParameterBinder.bindDeleteForIds(ps, employeeIds)
        );

        return result; // 成功件数。0なら削除なし
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<EmployeeEntity> downloadCsvEmployeeByIds(List<SimpleData> ids, String editor) {
        List<Integer> employeeIds = Utilities.createSequenceByIds(ids);
        String sql = EmployeeSqlBuilder.buildDownloadCsvEmployeeForIdsSql(employeeIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> EmployeeParameterBinder.bindFindAll(ps, null),
            EmployeeEntityMapper::map // ← ここで ResultSet を map
        );
    }
   
    /**
     * IDによる取得。
     * @param employeeId
     * @return IDから取得したEntityをかえす。
     */
    public EmployeeEntity findById(int employeeId) {
        String sql = EmployeeSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> EmployeeParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            employeeId
        );
    }

    /**
     * アカウントによる取得。
     * @param account
     * @return アカウントで指定したEntityを返す。
     */
    public EmployeeEntity findByAccount(String account) {
        String sql = EmployeeSqlBuilder.buildFindByAccountSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> EmployeeParameterBinder.bindFindByAccount(pstmt, comp),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            account
        );
    }

    /**
     * 全件取得。
     * @return 全てのEntityを返す。
     */
    public List<EmployeeEntity> findAll() {
        String sql = EmployeeSqlBuilder.buildFindAllSql();

        return sqlRepository.findAll(
            sql,
            ps -> EmployeeParameterBinder.bindFindAll(ps, null),
            EmployeeEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
