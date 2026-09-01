package com.kyouseipro.neo.domain.management.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.domain.management.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.model.TimeworkStatus;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeworkRepository {
    private final SqlRepository sqlRepository;

    public TimeworkStatus findTodayByAccount(String account, LocalDate workDate) {
        return sqlRepository.queryOneOrNull("""
            SELECT e.employee_id, e.full_name, e.office_id,
                   COALESCE(o.name, '') AS office_name,
                   t.timework_id, t.start_time, t.end_time
            FROM employees e
            LEFT JOIN offices o ON o.office_id = e.office_id AND o.state = 0
            LEFT JOIN timeworks t ON t.employee_id = e.employee_id
                 AND t.work_date = ? AND t.state = 0
            WHERE e.account = ? AND e.state = 0
            """, (ps, ignored) -> {
                ps.setObject(1, workDate);
                ps.setString(2, account);
            }, rs -> {
                Number idValue = (Number) rs.getObject("timework_id");
                Number officeValue = (Number) rs.getObject("office_id");
                LocalDateTime startTime = toLocalDateTime(rs.getTimestamp("start_time"));
                LocalDateTime endTime = toLocalDateTime(rs.getTimestamp("end_time"));
                return new TimeworkStatus(
                    idValue == null ? null : idValue.longValue(),
                    rs.getLong("employee_id"),
                    rs.getString("full_name"),
                    officeValue == null ? null : officeValue.longValue(),
                    rs.getString("office_name"),
                    workDate,
                    startTime,
                    endTime,
                    endTime != null ? "FINISHED" : startTime != null ? "WORKING" : "NOT_STARTED",
                    endTime != null ? "退勤済み" : startTime != null ? "勤務中" : "未出勤",
                    startTime == null,
                    startTime != null && endTime == null
                );
            }, null);
    }

    public List<TimeworkListItem> findList(LocalDate workDate, Long officeId, Long employeeId) {
        StringBuilder sql = new StringBuilder("""
            SELECT t.timework_id, t.employee_id, e.full_name, t.office_id,
                   COALESCE(o.name, '') AS office_name,
                   t.work_date, t.start_time, t.end_time
            FROM timeworks t
            INNER JOIN employees e ON e.employee_id = t.employee_id AND e.state = 0
            LEFT JOIN offices o ON o.office_id = t.office_id AND o.state = 0
            WHERE t.work_date = ? AND t.state = 0
            """);
        if (officeId != null) sql.append(" AND t.office_id = ?");
        if (employeeId != null) sql.append(" AND t.employee_id = ?");
        sql.append(" ORDER BY t.start_time, e.full_name");

        return sqlRepository.queryList(sql.toString(), (ps, ignored) -> {
            int index = 1;
            ps.setObject(index++, workDate);
            if (officeId != null) ps.setLong(index++, officeId);
            if (employeeId != null) ps.setLong(index, employeeId);
        }, rs -> {
            Number officeValue = (Number) rs.getObject("office_id");
            return new TimeworkListItem(
                rs.getLong("timework_id"), rs.getLong("employee_id"), rs.getString("full_name"),
                officeValue == null ? null : officeValue.longValue(), rs.getString("office_name"),
                rs.getObject("work_date", LocalDate.class),
                toLocalDateTime(rs.getTimestamp("start_time")),
                toLocalDateTime(rs.getTimestamp("end_time"))
            );
        }, null);
    }

    public long insertStart(long employeeId, Long officeId, LocalDate workDate, LocalDateTime stampedAt, String editor) {
        return sqlRepository.insert("""
            INSERT INTO timeworks(employee_id, office_id, work_date, start_time, regist_user, update_user)
            OUTPUT INSERTED.timework_id
            VALUES (?, ?, ?, ?, ?, ?)
            """, (ps, ignored) -> {
                ps.setLong(1, employeeId);
                if (officeId == null) ps.setNull(2, java.sql.Types.BIGINT); else ps.setLong(2, officeId);
                ps.setObject(3, workDate);
                ps.setTimestamp(4, Timestamp.valueOf(stampedAt));
                ps.setString(5, editor);
                ps.setString(6, editor);
            }, rs -> rs.getLong(1), null);
    }

    public int updateEnd(long timeworkId, LocalDateTime stampedAt, String editor) {
        return sqlRepository.updateRequired("""
            UPDATE timeworks
            SET end_time = ?, update_date = SYSDATETIME(), update_user = ?, version = version + 1
            WHERE timework_id = ? AND state = 0 AND start_time IS NOT NULL AND end_time IS NULL
            """, List.of(Timestamp.valueOf(stampedAt), editor, timeworkId),
            "すでに退勤済みです。画面を再読み込みしてください。");
    }

    private static LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
