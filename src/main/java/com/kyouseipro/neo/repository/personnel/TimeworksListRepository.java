package com.kyouseipro.neo.repository.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

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

    /**
     * IDによる取得。
     * @param employeeId
     * @return IDから取得した今日の日付のEntityをかえす。
     */
    public TimeworksListEntity findByTodaysEntityByEmployeeId(int employeeId) {
        String sql = TimeworksListSqlBuilder.buildFindByTodaysEntityByEmployeeId();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> TimeworksListParameterBinder.bindFindByTodaysEntityByEmployeeId(pstmt, comp),
            rs -> rs.next() ? TimeworksListEntityMapper.map(rs) : null,
            employeeId
        );
    }

    /**
     * 日付指定でIDで指定した従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findByEmployeeIdFromBetweenDate(int id, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindByBetweenEntityByEmployeeId();

        return sqlRepository.findAll(
            sql,
            ps -> TimeworksListParameterBinder.bindFindByBetweenEntityByEmployeeId(ps, id, start, end),
            TimeworksListEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定でIDで指定した従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksSummaryEntity> findByOfficeIdFromBetweenDate(int id, LocalDate start, LocalDate end) {
        String sql = TimeworksListSqlBuilder.buildFindByBetweenSummaryEntityByOfficeId();

        return sqlRepository.findAll(
            sql,
            ps -> TimeworksListParameterBinder.bindFindByBetweenSummaryEntityByOfficeId(ps, id, start, end),
            TimeworksSummaryEntityMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 日付指定で全従業員の勤怠情報を取得
     * @param date
     * @return
     */
    public List<TimeworksListEntity> findByDate(LocalDate date) {
        String sql = TimeworksListSqlBuilder.buildFindByDateSql();

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
    public Integer insertTimeworks(TimeworksListEntity t, String editor) {
        String sql = TimeworksListSqlBuilder.buildInsertSql();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> TimeworksListParameterBinder.bindInsertParameters(pstmt, entity, editor),
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
    public Integer updateTimeworks(TimeworksListEntity t, String editor) {
        String sql = TimeworksListSqlBuilder.buildUpdateSql();

        return sqlRepository.execute(
            sql,
            (pstmt, entity) -> TimeworksListParameterBinder.bindUpdateParameters(pstmt, entity, editor),
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
    public Integer updateTimeworksList(List<TimeworksListEntity> list, String editor) {
        // if (list.size() == 0) return 0;

        // String sql = "";
        // for (int i = 0; i < list.size(); i++) {
        //     sql += TimeworksListSqlBuilder.buildUpdateSql();
        // }

        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, entities) -> TimeworksListParameterBinder.bindUpdateListParameters(pstmt, entities, editor),
        //     rs -> rs.next() ? rs.getInt("timeworks_id") : null,
        //     list
        // );
        // public int updateTimeworksList(List<TimeworksListEntity> list, String editor) {
        if (list.isEmpty()) return 0;

        String sql = TimeworksListSqlBuilder.buildUpdateSql(); // UPDATE ... WHERE id = ?

        return sqlRepository.executeBatch(
            sql,
            (pstmt, entity) -> TimeworksListParameterBinder.bindUpdateParameters(pstmt, entity, editor),
            list
        );

    }
}
