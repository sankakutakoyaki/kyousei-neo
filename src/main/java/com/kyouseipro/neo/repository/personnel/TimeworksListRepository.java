package com.kyouseipro.neo.repository.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.mapper.personnel.TimeworksListEntityMapper;
import com.kyouseipro.neo.query.parameter.personnel.TimeworksListParameterBinder;
import com.kyouseipro.neo.query.parameter.personnel.WorkingConditionsParameterBinder;
import com.kyouseipro.neo.query.sql.personnel.TimeworksListSqlBuilder;
import com.kyouseipro.neo.query.sql.personnel.WorkingConditionsSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import groovyjarjarantlr4.v4.parse.ANTLRParser.terminal_return;
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
}
