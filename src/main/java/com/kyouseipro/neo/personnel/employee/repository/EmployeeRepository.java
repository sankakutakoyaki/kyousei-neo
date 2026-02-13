package com.kyouseipro.neo.personnel.employee.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntityRequest;
import com.kyouseipro.neo.personnel.employee.mapper.EmployeeEntityMapper;

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
    public EmployeeEntity findById(int id) {
        String sql = EmployeeSqlBuilder.buildFindById();

        int targetId = id;
        // IDが3桁以下の場合はCODEなのでCODEからIDを取得する
        if (String.valueOf(Math.abs(id)).length() < 4) {
            EmployeeEntity entity = findByCode(id);

            targetId = entity.getEmployeeId();
        }

        return sqlRepository.queryOne(
            sql,
            (ps, i) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, i);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            EmployeeEntityMapper::map,
            targetId
        );
    }

    /**
     * CODEによる取得。
     * @param code
     * @return CODEから取得したEntityをかえす。
     */
    public EmployeeEntity findByCode(int code) {
        String sql = EmployeeSqlBuilder.buildFindByCode();

        return sqlRepository.queryOne(
            sql,
            (ps, c) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, c);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            EmployeeEntityMapper::map,
            code
        );
    }

    /**
     * アカウントによる取得。
     * @param account
     * @return アカウントで指定したEntityを返す。
     */
    public EmployeeEntity findByAccount(String account) {
        String sql = EmployeeSqlBuilder.buildFindByAccount();

        return sqlRepository.queryOne(
            sql,
            (ps, a) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setString(index++, account);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            EmployeeEntityMapper::map,
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

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            EmployeeEntityMapper::map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @param editor
     * @return 新規IDを返す。
     */
    public int insert(EmployeeEntityRequest entity, String editor) {
        String sql = EmployeeSqlBuilder.buildBulkInsert(entity);
        
        try {
            return sqlRepository.insert(
                sql,
                (ps, en) -> EmployeeParameterBinder.bindBulkInsert(ps, en, editor),
                rs -> rs.getInt("employee_id"),
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
    public int update(EmployeeEntityRequest entity, String editor) {
        String sql = EmployeeSqlBuilder.buildBulkUpdate(entity);

        try {
            int count = sqlRepository.updateRequired(
                sql,
                (ps, e) -> EmployeeParameterBinder.bindBulkUpdate(ps, e, editor),
                entity
            );

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
            int count = sqlRepository.updateRequired(
                sql,
                (ps, v) -> EmployeeParameterBinder.bindUpdateCode(ps, id, code, editor)
            );

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

        int count = sqlRepository.updateRequired(
            sql,
            (ps, v) -> EmployeeParameterBinder.bindUpdatePhone(ps, id, phone, editor)
        );

        return count;
    }

    /**
     * 削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("削除対象が指定されていません");
        }

        String sql = EmployeeSqlBuilder.buildDeleteByIds(list.getIds().size());
        int count = sqlRepository.updateRequired(
            sql,
            (ps, e) -> EmployeeParameterBinder.bindDeleteByIds(ps, e.getIds(), editor),
            list
        );

        return count;
    }

    /**
     * CSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<EmployeeEntity> downloadCsvByIds(IdListRequest list, String editor) {
        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        String sql = EmployeeSqlBuilder.buildDownloadCsvByIds(list.getIds().size());
        return sqlRepository.queryList(
            sql,
            (ps, e) -> EmployeeParameterBinder.bindDownloadCsvByIds(ps, e.getIds()),
            EmployeeEntityMapper::map,
            list
        );
    }
}
