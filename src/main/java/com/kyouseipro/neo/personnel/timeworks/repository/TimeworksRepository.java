package com.kyouseipro.neo.personnel.timeworks.repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksEntity;
import com.kyouseipro.neo.personnel.timeworks.mapper.TimeworksEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeworksRepository {
    private final SqlRepository sqlRepository;

    public Optional<TimeworksEntity> findToday(Integer employeeId, LocalDate workBaseDate) {

        String sql = TimeworksSqlBuilder.buildFindToday();

        return sqlRepository.executeQuery(
            sql,
            (pstmt, p) -> {
                int index = 1;
                pstmt.setInt(index++, employeeId);
                pstmt.setDate(index++, Date.valueOf(workBaseDate));
            },
            rs -> rs.next() ? TimeworksEntityMapper.map(rs) : null,
            null
        );
    }

    public int insert(TimeworksEntity e) {

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
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        return sqlRepository.executeUpdate(
            sql,
            (pstmt) -> {
                int i = 1;
                pstmt.setInt(i++, e.getEmployeeId());
                pstmt.setTimestamp(i++, toTs(e.getStartDt()));
                pstmt.setTimestamp(i++, toTs(e.getEndDt()));
                pstmt.setInt(i++, defaultInt(e.getBreakMinutes(), 60));
                pstmt.setDate(i++, Date.valueOf(e.getWorkBaseDate()));

                pstmt.setBigDecimal(i++, e.getStartLatitude());
                pstmt.setBigDecimal(i++, e.getStartLongitude());
                pstmt.setBigDecimal(i++, e.getEndLatitude());
                pstmt.setBigDecimal(i++, e.getEndLongitude());

                pstmt.setInt(i++, e.getWorkType().getCode());
                pstmt.setInt(i++, e.getState().getCode());
            }
        );
    }

    public int update(TimeworksEntity e) {

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

        return sqlRepository.executeUpdate(
            sql,
            (pstmt) -> {
                int i = 1;
                pstmt.setTimestamp(i++, toTs(e.getStartDt()));
                pstmt.setTimestamp(i++, toTs(e.getEndDt()));
                pstmt.setInt(i++, defaultInt(e.getBreakMinutes(), 60));

                pstmt.setBigDecimal(i++, e.getStartLatitude());
                pstmt.setBigDecimal(i++, e.getStartLongitude());
                pstmt.setBigDecimal(i++, e.getEndLatitude());
                pstmt.setBigDecimal(i++, e.getEndLongitude());

                pstmt.setInt(i++, e.getWorkType().getCode());
                pstmt.setInt(i++, e.getState().getCode());

                pstmt.setInt(i++, e.getTimeworksId());
            }
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

    // public List<TimeworksEntity> findByBaseDate(Integer employeeId, LocalDate baseDate) {
    //     String sql =
    //         "SELECT * FROM timeworks " +
    //         "WHERE employee_id = ? AND work_base_date = ? " +
    //         "ORDER BY start_dt";

    //     return sqlRepository.findAll(
    //         sql,
    //         (pstmt, v) -> {
    //             pstmt.setInt(1, employeeId);
    //             pstmt.setDate(2, Date.valueOf(baseDate));
    //         },
    //         TimeworksEntityMapper::map // ← ここで ResultSet を map
    //     );
    // }

    public List<TimeworksEntity> findAllByBaseDate(LocalDate baseDate) {
        // String sql =
        //     "SELECT * FROM timeworks " +
        //     "WHERE work_base_date = ? " +
        //     "ORDER BY employee_id";
        String sql = TimeworksSqlBuilder.buildFindAllByBaseDate();
        return sqlRepository.findAll(
            sql,
            (pstmt, v) -> {
                int index = 1;
                pstmt.setDate(index++, Date.valueOf(baseDate));
            },
            TimeworksEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // /**
    //  * 新規
    //  * @param t
    //  * @return
    //  */
    // public Integer insertStart(TimeworksEntity t) {

    //     String sql =
    //         "INSERT INTO timeworks (" +
    //         " employee_id, start_dt, break_minutes, work_base_date, work_type, state" +
    //         ") OUTPUT INSERTED.timeworks_id VALUES (?, ?, ?, ?, ?, ?)";

    //     return sqlRepository.executeRequired(
    //         sql,
    //         (pstmt, e) -> {
    //             int i = 1;
    //             pstmt.setInt(i++, e.getEmployeeId());
    //             pstmt.setTimestamp(i++, Timestamp.valueOf(e.getStartDt()));
    //             pstmt.setInt(i++, e.getBreakMinutes());
    //             pstmt.setDate(i++, Date.valueOf(e.getWorkBaseDate()));
    //             pstmt.setInt(i++, e.getWorkType());
    //             pstmt.setInt(i++, 0); // 打刻中
    //         },
    //         rs -> rs.next() ? rs.getInt(1) : null,
    //         t
    //     );
    // }

    // /**
    //  * 更新
    //  * @param t
    //  */
    // public void updateEnd(TimeworksEntity t) {

    //     String sql =
    //         "UPDATE timeworks SET " +
    //         " end_dt = ?, state = 1, update_date = SYSDATETIME()" +
    //         " WHERE timeworks_id = ?";

    //     sqlRepository.executeRequired(
    //         sql,
    //         (pstmt, e) -> {
    //             int i = 1;
    //             pstmt.setTimestamp(i++, Timestamp.valueOf(e.getEndDt()));
    //             pstmt.setInt(i++, e.getTimeworksId());
    //         },
    //         pstmt -> null,
    //         t
    //     );
    // }

    // /**
    //  * 打刻中データ取得
    //  * @param employeeId
    //  * @return
    //  */
    // public Optional<TimeworksEntity> findWorking(Integer employeeId) {

    //     String sql =
    //         "SELECT TOP 1 * FROM timeworks " +
    //         "WHERE employee_id = ? AND state = 0 " +
    //         "ORDER BY start_dt DESC";

    //     return sqlRepository.executeQuery(
    //         sql,
    //         (pstmt, e) -> pstmt.setInt(1, employeeId),
    //         rs -> rs.next() ? TimeworksEntityMapper.map(rs) : null,
    //         null
    //     );
    // }
}
