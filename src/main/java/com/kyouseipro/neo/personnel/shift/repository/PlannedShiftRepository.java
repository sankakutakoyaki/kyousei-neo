package com.kyouseipro.neo.personnel.shift.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.dto.sql.repository.SqlRepository;
import com.kyouseipro.neo.personnel.shift.entity.PlannedShift;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlannedShiftRepository {

    private final SqlRepository sqlRepository;

    public List<PlannedShift> findByMonth(LocalDate start, LocalDate end) {

        String sql = """
            SELECT planned_shift_id, employee_id, shift_date, shift_type
            FROM planned_shift
            WHERE shift_date BETWEEN ? AND ?
        """;

        return sqlRepository.queryList(
            sql,
            (ps, v) -> {
                ps.setDate(1, Date.valueOf(start));
                ps.setDate(2, Date.valueOf(end));
            },
            rs -> {
                PlannedShift s = new PlannedShift();
                s.setPlannedShiftId(rs.getLong("planned_shift_id"));
                s.setEmployeeId(rs.getLong("employee_id"));
                s.setShiftDate(rs.getDate("shift_date").toLocalDate());
                s.setShiftType(rs.getString("shift_type"));
                return s;
            }
        );
    }

    public void insert(Long employeeId, LocalDate date, String type) {

        String sql = """
            INSERT INTO planned_shift (employee_id, shift_date, shift_type)
            VALUES (?, ?, ?)
        """;

        sqlRepository.update(
            sql,
            (ps, v) -> {
                ps.setLong(1, employeeId);
                ps.setDate(2, Date.valueOf(date));
                ps.setString(3, type);
            }
        );
    }

    public void update(Long employeeId, LocalDate date, String type) {

        String sql = """
            UPDATE planned_shift
            SET shift_type = ?, updated_at = SYSDATETIME()
            WHERE employee_id = ? AND shift_date = ?
        """;

        sqlRepository.update(
            sql,
            (ps, v) -> {
                ps.setString(1, type);
                ps.setLong(2, employeeId);
                ps.setDate(3, Date.valueOf(date));
                
            }
        );
    }

    public void delete(Long employeeId, LocalDate date) {

        String sql = """
            DELETE FROM planned_shift
            WHERE employee_id = ? AND shift_date = ?
        """;

        sqlRepository.update(
            sql,
            (ps, v) -> {
                ps.setLong(1, employeeId);
                ps.setDate(2, Date.valueOf(date));
            }
        );
    }

    record ExistsParam(Long employeeId, LocalDate date) {}

    public boolean exists(Long employeeId, LocalDate date) {

        String sql = """
            SELECT TOP 1 1
            FROM planned_shift
            WHERE employee_id = ?
            AND shift_date = ?
        """;
        return sqlRepository.exists(
            sql,
            (ps, p) -> {
                ps.setLong(1, p.employeeId());
                ps.setDate(2, Date.valueOf(p.date()));
            },
            new ExistsParam(employeeId, date)
        );
    }
}