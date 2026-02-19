package com.kyouseipro.neo.personnel.timeworks.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksEntity;
import com.kyouseipro.neo.personnel.timeworks.mapper.TimeworksEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeworksRepository {
    private final SqlRepository sqlRepository;

    public TimeworksEntity findToday(Integer id, LocalDate workBaseDate) {
        String sql = TimeworksSqlBuilder.buildFindToday();

        return sqlRepository.queryOneOrNull(
            sql,
            (pstmt, p) -> {
                int index = 1;
                pstmt.setInt(index++, id);
                pstmt.setDate(index++, Date.valueOf(workBaseDate));
            },
            TimeworksEntityMapper::map
        );
    }

    public int insert(TimeworksEntity entity) {

        String sql = """
            INSERT INTO timeworks (
                employee_id,
                start_dt,
                end_dt,
                break_minutes,
                work_base_date,
                start_latitude,
                start_longitude,
                end_latitude,
                end_longitude,
                work_type,
                state
            ) 
            OUTPUT INSERTED.timeworks_id 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try {
            return sqlRepository.insert(
                sql,
                (ps, e) -> {
                    int i = 1;
                    ps.setInt(i++, e.getEmployeeId());
                    ps.setTimestamp(i++, toTs(e.getStartDt()));
                    ps.setTimestamp(i++, toTs(e.getEndDt()));
                    ps.setInt(i++, defaultInt(e.getBreakMinutes(), 60));
                    ps.setDate(i++, Date.valueOf(e.getWorkBaseDate()));
                    ps.setBigDecimal(i++, e.getStartLatitude());
                    ps.setBigDecimal(i++, e.getStartLongitude());
                    ps.setBigDecimal(i++, e.getEndLatitude());
                    ps.setBigDecimal(i++, e.getEndLongitude());
                    ps.setInt(i++, e.getWorkType().getCode());
                    ps.setInt(i++, e.getState().getCode());
                },
                rs -> rs.getInt("timeworks_id"),
                entity
            );
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public int update(TimeworksEntity entity) {

        String sql = """
            UPDATE timeworks
            SET
                start_dt = ?,
                end_dt = ?,
                break_minutes = ?,
                start_latitude = ?,
                start_longitude = ?,
                end_latitude = ?,
                end_longitude = ?,
                work_type = ?,
                state = ?,
                update_date = SYSDATETIME()
            WHERE timeworks_id = ?
        """;

        return sqlRepository.update(
            sql,
            (ps, e) -> {
                int i = 1;
                ps.setTimestamp(i++, toTs(e.getStartDt()));
                ps.setTimestamp(i++, toTs(e.getEndDt()));
                ps.setInt(i++, defaultInt(e.getBreakMinutes(), 60));
                ps.setBigDecimal(i++, e.getStartLatitude());
                ps.setBigDecimal(i++, e.getStartLongitude());
                ps.setBigDecimal(i++, e.getEndLatitude());
                ps.setBigDecimal(i++, e.getEndLongitude());
                ps.setInt(i++, e.getWorkType().getCode());
                ps.setInt(i++, e.getState().getCode());
                ps.setInt(i++, e.getTimeworksId());
            },
            entity
        );
    }

    /**
     * Null安全ユーティリティ
     * @param dt
     * @return
     */
    private Timestamp toTs(LocalDateTime dt) {
        return dt == null ? null : Timestamp.valueOf(dt);
    }
    private int defaultInt(Integer v, int def) {
        return v == null ? def : v;
    }

    public List<TimeworksEntity> findAllByBaseDate(LocalDate baseDate) {
        String sql = TimeworksSqlBuilder.buildFindAllByBaseDate();

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                int index = 1;
                ps.setDate(index++, Date.valueOf(baseDate));
            },
            TimeworksEntityMapper::map
        );
    }
}
