package com.kyouseipro.neo.repository.personnel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SqlExceptionUtil;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksSummaryEntity;
import com.kyouseipro.neo.mapper.personnel.TimeworksListEntityMapper;
import com.kyouseipro.neo.mapper.personnel.TimeworksSummaryEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.TimeworksListParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.TimeworksListSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeworksListRepository {
    private final SqlRepository sqlRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得した今日の日付のEntityをかえす。
     */
    public Optional<TimeworksListEntity> findByTodaysByEmployeeId(int id) {
        String sql = TimeworksListSqlBuilder.buildFindByTodaysByEmployeeId();
        // EmployeeEntity entity = employeeRepository.findById(id);

        // int targetId = id;
        EmployeeEntity entity = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("社員が見つかりません: " + id));
        int targetId = entity.getEmployee_id();
        // int id = entity.getEmployee_id();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> TimeworksListParameterBinder.bindFindByTodaysByEmployeeId(ps, en),
            rs -> rs.next() ? TimeworksListEntityMapper.map(rs) : null,
            targetId
        );
    }

    /**
     * 日付指定でIDで指定した従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findByBetweenByEmployeeId(int id, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindByBetweenByEmployeeId();
        // EmployeeEntity entity = employeeRepository.findById(employeeId);
        // int id = entity.getEmployee_id();
        EmployeeEntity entity = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("社員が見つかりません: " + id));
        int targetId = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> TimeworksListParameterBinder.bindFindByBetweenByEmployeeId(ps, targetId, start, end),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定でIDで指定した従業員の確定済みも含めた勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findAllByBetweenByEmployeeId(int id, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindAllByBetweenByEmployeeId();
        // EmployeeEntity entity = employeeRepository.findById(employeeId);
        // int id = entity.getEmployee_id();
        EmployeeEntity entity = employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("社員が見つかりません: " + id));
        int targetId = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> TimeworksListParameterBinder.bindFindAllByBetweenByEmployeeId(ps, targetId, start, end),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定で従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksSummaryEntity> findByBetweenSummary(LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindByBetweenSummary();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> TimeworksListParameterBinder.bindFindByBetweenSummary(ps, start, end),
            TimeworksSummaryEntityMapper::map // ← ここで ResultSet を map
        );
    }
    
    /**
     * 日付指定でIDで指定した営業所の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksSummaryEntity> findByBetweenSummaryByOfficeId(int id, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindByBetweenSummaryByOfficeId();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> TimeworksListParameterBinder.bindFindByBetweenSummaryByOfficeId(ps, id, start, end),
            TimeworksSummaryEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定で全従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findByDate(LocalDate date) {
        String sql = TimeworksListSqlBuilder.buildFindByDate();

        return sqlRepository.findAll(
            sql,
            (ps, v) -> TimeworksListParameterBinder.bindFindByDate(ps, date),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 新規作成。
     * @param entity
     * @return 新規IDを返す。
     */
    public int insert(TimeworksListEntity entity, String editor) {
        String sql = TimeworksListSqlBuilder.buildInsert();

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, entity) -> TimeworksListParameterBinder.bindInsert(pstmt, entity, editor),
        //     rs -> rs.next() ? rs.getInt("timeworks_id") : null,
        //     t
        // );
        try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> TimeworksListParameterBinder.bindInsert(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("timeworks_id");

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
     * @return 成功件数を返す。
     */
    public int update(TimeworksListEntity entity, String editor) {
        String sql = TimeworksListSqlBuilder.buildUpdate();

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, entity) -> TimeworksListParameterBinder.bindUpdate(pstmt, entity, editor),
        //     rs -> rs.next() ? rs.getInt("timeworks_id") : null,
        //     t
        // );
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> TimeworksListParameterBinder.bindUpdate(ps, entity, editor)
            );

            if (count == 0) {
                throw new BusinessException("更新対象が存在しません");
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
     * Stateを新規に変更
     * @param id
     * @param editor
     * @return
     */
    public Integer reverseConfirm(int id, String editor) {
        String sql = TimeworksListSqlBuilder.buildReverseConfirm();

    //    Integer result = sqlRepository.executeUpdate(
    //         sql,
    //         ps -> TimeworksListParameterBinder.bindReverseConfirm(ps, id, editor)
    //     );
        
    //     return result; // 成功件数。0なら削除なし
        try {
            int count = sqlRepository.executeUpdate(
                sql,
                ps -> TimeworksListParameterBinder.bindReverseConfirm(ps, id, editor)
            );

            if (count == 0) {
                throw new BusinessException("更新対象が存在しません");
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
     * まとめて更新
     * @param list
     * @param editor
     * @return
     */
    public int updateList(List<TimeworksListEntity> list, String editor) {
        if (list.isEmpty()) return 0;

        String sql = TimeworksListSqlBuilder.buildUpdate(); // UPDATE ... WHERE id = ?

        // return sqlRepository.executeBatch(
        //     sql,
        //     (ps, en) -> TimeworksListParameterBinder.bindUpdate(ps, en, editor),
        //     list
        // );
        try {
            int count = sqlRepository.executeBatch(
                sql,
                (ps, en) -> TimeworksListParameterBinder.bindUpdate(ps, en, editor),
                list
            );

            if (count == 0) {
                throw new BusinessException("更新対象が存在しません");
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
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<TimeworksListEntity> downloadCsvByIdsFromBetween(List<SimpleData> list, String start, String end, String editor) {
        // List<Integer> ids = Utilities.createSequenceByIds(list);
        // String sql = TimeworksListSqlBuilder.buildDownloadCsvForIdsFromBetween(ids.size());

        // return sqlRepository.findAll(
        //     sql,
        //     (ps, v) -> TimeworksListParameterBinder.bindDownloadCsvByIdsFromBetween(ps, employeeIds, start, end),
        //     TimeworksListEntityMapper::map // ← ここで ResultSet を map
        // );
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        List<Integer> ids = Utilities.createSequenceByIds(list);
        String sql = TimeworksListSqlBuilder.buildDownloadCsvForIdsFromBetween(ids.size());

        return sqlRepository.findAll(
            sql,
            (ps, v) ->  TimeworksListParameterBinder.bindDownloadCsvByIdsFromBetween(ps, ids, start, end),
            TimeworksListEntityMapper::map
        );
    }
}
