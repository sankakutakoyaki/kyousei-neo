package com.kyouseipro.neo.repository.personnel;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
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
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityをかえす。
     */
    public Optional<EmployeeEntity> findById(int id) {
        String sql = EmployeeSqlBuilder.buildFindById();

        int targetId = id;
        // IDが3桁以下の場合はCODEなのでCODEからIDを取得する
        if (String.valueOf(Math.abs(id)).length() < 4) {
            EmployeeEntity entity = findByCode(id)
                .orElseThrow(() -> new RuntimeException("従業員が見つかりません: " + id));

            targetId = entity.getEmployee_id();
        }

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> EmployeeParameterBinder.bindFindById(ps, en),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            targetId
        );
    }

    /**
     * CODEによる取得。
     * @param code
     * @return CODEから取得したEntityをかえす。
     */
    public Optional<EmployeeEntity> findByCode(int code) {
        String sql = EmployeeSqlBuilder.buildFindByCode();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> EmployeeParameterBinder.bindFindByCode(ps, en),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            code
        );
    }

    /**
     * アカウントによる取得。
     * @param account
     * @return アカウントで指定したEntityを返す。
     */
    public Optional<EmployeeEntity> findByAccount(String account) {
        String sql = EmployeeSqlBuilder.buildFindByAccount();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> EmployeeParameterBinder.bindFindByAccount(ps, en),
            rs -> rs.next() ? EmployeeEntityMapper.map(rs) : null,
            account
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<EmployeeEntity> findAll() {
        String sql = EmployeeSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> EmployeeParameterBinder.bindFindAll(ps, null),
            EmployeeEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @param editor
     * @return 新規IDを返す。
     */
    public int insert(EmployeeEntity entity, String editor) {
        String sql = EmployeeSqlBuilder.buildInsert();

        // return sqlRepository.executeRequired(
        //     sql,
        //     (pstmt, emp) -> EmployeeParameterBinder.bindInsert(pstmt, emp, editor),
        //     rs -> rs.next() ? rs.getInt("employee_id") : null,
        //     employee
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> EmployeeParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("employee_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
                entity
            );
        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 更新。
     * @param entity
     * @param editor
     * @return 成功件数を返す。
     */
    public int update(EmployeeEntity entity, String editor) {
        String sql = EmployeeSqlBuilder.buildUpdate();

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     pstmt -> EmployeeParameterBinder.bindUpdate(pstmt, employee, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> EmployeeParameterBinder.bindUpdate(ps, entity, editor)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
            }

            return count;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * コードを更新。
     * @param id
     * @param code
     * @return 成功件数を返す。
     */
    public int updateCode(int id, int code, String editor) {
        String sql = EmployeeSqlBuilder.buildUpdateCode();
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> EmployeeParameterBinder.bindUpdateCode(ps, id, code, editor)
            );

            if (count == 0) {
                throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
            }

            return count;

        } catch (RuntimeException e) {
            if (SqlExceptionUtil.isDuplicateKey(e)) {
                throw new BusinessException("このコードはすでに使用されています。");
            }
            throw e;
        }
    }

    /**
     * 電話番号を更新。
     * @param id
     * @param phone
     * @return 成功件数を返す。
     */
    public int updatePhone(int id, String phone, String editor) {
        String sql = EmployeeSqlBuilder.buildUpdatePhone();

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> EmployeeParameterBinder.bindUpdatePhone(ps, id, phone, editor)
        );

        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * 削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String editor) {
        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = EmployeeSqlBuilder.buildDeleteByIds(ids.size());

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> EmployeeParameterBinder.bindDeleteByIds(ps, employeeIds, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        int count = sqlRepository.executeUpdate(
            sql,
            ps -> EmployeeParameterBinder.bindDeleteByIds(ps, ids, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<EmployeeEntity> downloadCsvByIds(List<SimpleData>list, String editor) {
        // List<Integer> ids = Utilities.createSequenceByIds(list);
        // String sql = EmployeeSqlBuilder.buildDownloadCsvByIds(ids.size());

        // return sqlRepository.findAll(
        //     sql,
        //     ps -> EmployeeParameterBinder.bindDownloadCsvByIds(ps, employeeIds),
        //     EmployeeEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = EmployeeSqlBuilder.buildDownloadCsvByIds(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) -> EmployeeParameterBinder.bindDownloadCsvByIds(ps, ids),
            EmployeeEntityMapper::map
        );
    }
}
