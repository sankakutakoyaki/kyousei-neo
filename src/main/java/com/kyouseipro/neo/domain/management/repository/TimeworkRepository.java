package com.kyouseipro.neo.domain.management.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.domain.management.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.model.TimeworkStatus;
import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TimeworkRepository {
    private final SqlRepository sqlRepository;

    public TimeworkStatus findTodayByIdentifier(String identifier, LocalDate workDate) {
        return sqlRepository.queryOneOrNull("""
            SELECT TOP (1) e.employee_id, e.full_name, e.office_id,
                   COALESCE(o.name, '') AS office_name,
                   t.timework_id, t.start_time, t.end_time
            FROM employees e
            LEFT JOIN offices o ON o.office_id = e.office_id AND o.state = 0
            LEFT JOIN timeworks t ON t.employee_id = e.employee_id
                 AND t.state = 0
                 AND (
                     CAST(t.start_time AS DATE) = ?
                     OR (t.end_time IS NULL AND t.start_time >= DATEADD(HOUR, -24, SYSDATETIME()))
                 )
            WHERE e.state = 0
              AND (CONVERT(NVARCHAR(30), e.employee_id) = ? OR CONVERT(NVARCHAR(100), e.code) = ?)
            ORDER BY
                CASE WHEN CONVERT(NVARCHAR(30), e.employee_id) = ? THEN 0 ELSE 1 END,
                CASE WHEN t.start_time IS NOT NULL AND t.end_time IS NULL THEN 0 ELSE 1 END,
                t.start_time DESC
            """, (ps, ignored) -> {
                ps.setObject(1, workDate);
                ps.setString(2, identifier);
                ps.setString(3, identifier);
                ps.setString(4, identifier);
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

    public TimeworkStatus findTodayByEmployeeId(long employeeId, LocalDate workDate) {
        return findTodayByIdentifier(Long.toString(employeeId), workDate);
    }

    public TimeworkStatus findTodayByAccount(String account, LocalDate workDate) {
        return sqlRepository.queryOneOrNull("""
            SELECT TOP (1) e.employee_id, e.full_name, e.office_id,
                   COALESCE(o.name, '') AS office_name,
                   t.timework_id, t.start_time, t.end_time
            FROM employees e
            LEFT JOIN offices o ON o.office_id = e.office_id AND o.state = 0
            LEFT JOIN timeworks t ON t.employee_id = e.employee_id
                 AND t.state = 0
                 AND (
                     CAST(t.start_time AS DATE) = ?
                     OR (t.end_time IS NULL AND t.start_time >= DATEADD(HOUR, -24, SYSDATETIME()))
                 )
            WHERE e.state = 0 AND e.account = ?
            ORDER BY
                CASE WHEN t.start_time IS NOT NULL AND t.end_time IS NULL THEN 0 ELSE 1 END,
                t.start_time DESC
            """, (ps, ignored) -> {
                ps.setObject(1, workDate);
                ps.setString(2, account);
            }, rs -> toStatus(rs, workDate), null);
    }

    public List<TimeworkListItem> findList(LocalDate workDate, Long officeId, Long employeeId) {
        StringBuilder sql = new StringBuilder("""
            SELECT t.timework_id, t.employee_id, e.full_name, e.office_id,
                   COALESCE(o.name, '') AS office_name,
                   CAST(t.start_time AS DATE) AS work_date, t.start_time, t.end_time, t.version
            FROM timeworks t
            INNER JOIN employees e ON e.employee_id = t.employee_id AND e.state = 0
            LEFT JOIN offices o ON o.office_id = e.office_id AND o.state = 0
            WHERE t.state = 0
              AND (
                  CAST(t.start_time AS DATE) = ?
                  OR CAST(t.end_time AS DATE) = ?
                  OR (t.end_time IS NULL AND t.start_time >= DATEADD(HOUR, -24, SYSDATETIME()))
              )
            """);
        if (officeId != null) sql.append(" AND e.office_id = ?");
        if (employeeId != null) sql.append(" AND t.employee_id = ?");
        sql.append(" ORDER BY t.start_time, e.full_name");

        return sqlRepository.queryList(sql.toString(), (ps, ignored) -> {
            int index = 1;
            ps.setObject(index++, workDate);
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
                toLocalDateTime(rs.getTimestamp("end_time")), rs.getInt("version")
            );
        }, null);
    }

    public List<TimeworkListItem> findManagementList(LocalDate from, LocalDate to, Long officeId, Long employeeId) {
        StringBuilder sql = new StringBuilder("""
            SELECT t.timework_id, t.employee_id, e.full_name, e.office_id,
                   COALESCE(o.name, '') AS office_name,
                   t.work_date, t.start_time, t.end_time, t.version
            FROM timeworks t
            INNER JOIN employees e ON e.employee_id = t.employee_id AND e.state = 0
            LEFT JOIN offices o ON o.office_id = e.office_id AND o.state = 0
            WHERE t.work_date BETWEEN ? AND ? AND t.state = 0
            """);
        if (officeId != null) sql.append(" AND e.office_id = ?");
        if (employeeId != null) sql.append(" AND e.employee_id = ?");
        sql.append(" ORDER BY t.work_date, e.office_id, e.full_name");
        return sqlRepository.queryList(sql.toString(), (ps, ignored) -> {
            ps.setObject(1, from);
            ps.setObject(2, to);
            int index = 3;
            if (officeId != null) ps.setLong(index++, officeId);
            if (employeeId != null) ps.setLong(index, employeeId);
        }, rs -> {
            Number officeValue = (Number) rs.getObject("office_id");
            return new TimeworkListItem(
                rs.getLong("timework_id"), rs.getLong("employee_id"), rs.getString("full_name"),
                officeValue == null ? null : officeValue.longValue(), rs.getString("office_name"),
                rs.getObject("work_date", LocalDate.class),
                toLocalDateTime(rs.getTimestamp("start_time")),
                toLocalDateTime(rs.getTimestamp("end_time")), rs.getInt("version")
            );
        }, null);
    }

    public List<ComboDto> findEmployeeCombo(Long officeId) {
        StringBuilder sql = new StringBuilder("""
            SELECT employee_id, code, full_name, office_id
            FROM employees
            WHERE state = 0
            """);
        if (officeId != null) sql.append(" AND office_id = ?");
        sql.append(" ORDER BY full_name, employee_id");
        return sqlRepository.queryList(sql.toString(), (ps, ignored) -> {
            if (officeId != null) ps.setLong(1, officeId);
        }, rs -> {
            Number officeValue = (Number) rs.getObject("office_id");
            return new ComboDto(
                rs.getLong("employee_id"),
                rs.getString("code") + " " + rs.getString("full_name"),
                officeValue == null ? null : officeValue.longValue()
            );
        }, null);
    }

    public int updateTimes(long timeworkId, LocalDateTime startTime, LocalDateTime endTime, int version, String editor) {
        return sqlRepository.updateRequired("""
            UPDATE timeworks
            SET work_date = CAST(? AS DATE), start_time = ?, end_time = ?, update_date = SYSDATETIME(),
                update_user = ?, version = version + 1
            WHERE timework_id = ? AND version = ? AND state = 0
            """, java.util.Arrays.asList(
                Timestamp.valueOf(startTime),
                startTime == null ? null : Timestamp.valueOf(startTime),
                endTime == null ? null : Timestamp.valueOf(endTime),
                editor, timeworkId, version
            ), "他のユーザーに更新されています。再検索してください。");
    }

    public long insertStart(long employeeId, LocalDate workDate, LocalDateTime stampedAt, String editor) {
        return sqlRepository.insert("""
            INSERT INTO timeworks(employee_id, start_time, regist_user, update_user)
            OUTPUT INSERTED.timework_id
            VALUES (?, ?, ?, ?)
            """, (ps, ignored) -> {
                ps.setLong(1, employeeId);
                ps.setTimestamp(2, Timestamp.valueOf(stampedAt));
                ps.setString(3, editor);
                ps.setString(4, editor);
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

    private static TimeworkStatus toStatus(java.sql.ResultSet rs, LocalDate workDate) throws java.sql.SQLException {
        Number idValue = (Number) rs.getObject("timework_id");
        Number officeValue = (Number) rs.getObject("office_id");
        LocalDateTime startTime = toLocalDateTime(rs.getTimestamp("start_time"));
        LocalDateTime endTime = toLocalDateTime(rs.getTimestamp("end_time"));
        return new TimeworkStatus(
            idValue == null ? null : idValue.longValue(), rs.getLong("employee_id"),
            rs.getString("full_name"), officeValue == null ? null : officeValue.longValue(),
            rs.getString("office_name"), workDate, startTime, endTime,
            endTime != null ? "FINISHED" : startTime != null ? "WORKING" : "NOT_STARTED",
            endTime != null ? "退勤済み" : startTime != null ? "勤務中" : "未出勤",
            startTime == null, startTime != null && endTime == null
        );
    }
}
