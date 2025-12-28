package com.kyouseipro.neo.repository.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
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
     * @param employeeId
     * @return IDから取得した今日の日付のEntityをかえす。
     */
    public TimeworksListEntity findByTodaysByEmployeeId(int employeeId) {
        String sql = TimeworksListSqlBuilder.buildFindByTodaysByEmployeeId();
        EmployeeEntity entity = employeeRepository.findById(employeeId);
        int id = entity.getEmployee_id();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> TimeworksListParameterBinder.bindFindByTodaysByEmployeeId(pstmt, comp),
            rs -> rs.next() ? TimeworksListEntityMapper.map(rs) : null,
            id
        );
    }

    /**
     * 日付指定でIDで指定した従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findByBetweenByEmployeeId(int employeeId, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindByBetweenByEmployeeId();
        EmployeeEntity entity = employeeRepository.findById(employeeId);
        int id = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            ps -> TimeworksListParameterBinder.bindFindByBetweenByEmployeeId(ps, id, start, end),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定でIDで指定した従業員の確定済みも含めた勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findAllByBetweenByEmployeeId(int employeeId, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindAllByBetweenByEmployeeId();
        EmployeeEntity entity = employeeRepository.findById(employeeId);
        int id = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            ps -> TimeworksListParameterBinder.bindFindAllByBetweenByEmployeeId(ps, id, start, end),
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
            ps -> TimeworksListParameterBinder.bindFindByBetweenSummary(ps, start, end),
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
            ps -> TimeworksListParameterBinder.bindFindByBetweenSummaryByOfficeId(ps, id, start, end),
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
            ps -> TimeworksListParameterBinder.bindFindByDate(ps, date),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * INSERT
     * @param t
     * @param editor
     * @return
     */
    public Integer insert(TimeworksListEntity t, String editor) {
        String sql = TimeworksListSqlBuilder.buildInsert();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> TimeworksListParameterBinder.bindInsert(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("timeworks_id") : null,
            t
        );
    }

    /**
     * UPDATE
     * @param w
     * @param editor
     * @return
     */
    public Integer update(TimeworksListEntity t, String editor) {
        String sql = TimeworksListSqlBuilder.buildUpdate();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> TimeworksListParameterBinder.bindUpdate(pstmt, entity, editor),
            rs -> rs.next() ? rs.getInt("timeworks_id") : null,
            t
        );
    }

    /**
     * UPDATE
     * @param id
     * @param editor
     * @return
     */
    public Integer reverseConfirm(int id, String editor) {
        String sql = TimeworksListSqlBuilder.buildReverseConfirm();

       Integer result = sqlRepository.executeUpdate(
            sql,
            ps -> TimeworksListParameterBinder.bindReverseConfirm(ps, id, editor)
        );
        
        return result; // 成功件数。0なら削除なし
    }

    /**
     * UPDATE
     * @param w
     * @param editor
     * @return
     */
    public Integer updateList(List<TimeworksListEntity> list, String editor) {
        if (list.isEmpty()) return 0;

        String sql = TimeworksListSqlBuilder.buildUpdate(); // UPDATE ... WHERE id = ?

        return sqlRepository.executeBatch(
            sql,
            (pstmt, entity) -> TimeworksListParameterBinder.bindUpdate(pstmt, entity, editor),
            list
        );
    }

    /**
     * CSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<TimeworksListEntity> downloadCsvByIdsFromBetween(List<SimpleData> ids, String start, String end, String editor) {
        List<Integer> employeeIds = Utilities.createSequenceByIds(ids);
        String sql = TimeworksListSqlBuilder.buildDownloadCsvForIdsFromBetween(employeeIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> TimeworksListParameterBinder.bindDownloadCsvByIdsFromBetween(ps, employeeIds, start, end),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }
}
