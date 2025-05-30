package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.person.WorkingConditionsEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkingConditionsRepository {

    private final SqlRepository sqlRepository;

    // INSERT
    public Integer insert(WorkingConditionsEntity w, String editor) {
        String sql =
            "DECLARE @Inserted TABLE (working_conditions_id INT);" +
            "INSERT INTO working_conditions (employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "OUTPUT INSERTED.working_conditions_id INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO working_conditions_log (working_conditions_id, editor, process, log_date, employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "SELECT w.working_conditions_id, ?, 'INSERT', CURRENT_TIMESTAMP, w.employee_id, w.code, w.category, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.basic_start_time, w.basic_end_time, w.version, w.state " +
            "FROM working_conditions w INNER JOIN @Inserted i ON w.working_conditions_id = i.working_conditions_id;" +
            "SELECT working_conditions_id FROM @Inserted;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, w.getEmployee_id());
                pstmt.setInt(2, w.getCode());
                pstmt.setInt(3, w.getCategory());
                pstmt.setInt(4, w.getPayment_method());
                pstmt.setInt(5, w.getPay_type());
                pstmt.setInt(6, w.getBase_salary());
                pstmt.setInt(7, w.getTrans_cost());
                pstmt.setTime(8, Time.valueOf(w.getBasic_start_time()));
                pstmt.setTime(9, Time.valueOf(w.getBasic_end_time()));
                pstmt.setInt(10, w.getVersion());
                pstmt.setInt(11, w.getState());

                pstmt.setString(12, editor);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("working_conditions_id");
                    }
                }

                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // UPDATE
    public Integer update(WorkingConditionsEntity w, String editor) {
        String sql =
            "DECLARE @Updated TABLE (working_conditions_id INT);" +
            "UPDATE working_conditions SET employee_id=?, code=?, category=?, payment_method=?, pay_type=?, base_salary=?, trans_cost=?, basic_start_time=?, basic_end_time=?, version=?, state=? " +
            "OUTPUT INSERTED.working_conditions_id INTO @Updated " +
            "WHERE working_conditions_id=?;" +
            "INSERT INTO working_conditions_log (working_conditions_id, editor, process, log_date, employee_id, code, category, payment_method, pay_type, base_salary, trans_cost, basic_start_time, basic_end_time, version, state) " +
            "SELECT w.working_conditions_id, ?, 'UPDATE', CURRENT_TIMESTAMP, w.employee_id, w.code, w.category, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.basic_start_time, w.basic_end_time, w.version, w.state " +
            "FROM working_conditions w INNER JOIN @Updated u ON w.working_conditions_id = u.working_conditions_id;" +
            "SELECT working_conditions_id FROM @Updated;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, w.getEmployee_id());
                pstmt.setInt(2, w.getCode());
                pstmt.setInt(3, w.getCategory());
                pstmt.setInt(4, w.getPayment_method());
                pstmt.setInt(5, w.getPay_type());
                pstmt.setInt(6, w.getBase_salary());
                pstmt.setInt(7, w.getTrans_cost());
                pstmt.setTime(8, Time.valueOf(w.getBasic_start_time()));
                pstmt.setTime(9, Time.valueOf(w.getBasic_end_time()));
                pstmt.setInt(10, w.getVersion());
                pstmt.setInt(11, w.getState());

                pstmt.setInt(12, w.getWorking_conditions_id());

                pstmt.setString(13, editor);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("working_conditions_id");
                    }
                }

                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // IDで検索
    public WorkingConditionsEntity findById(int id) {
        String sql = "SELECT * FROM working_conditions WHERE working_conditions_id = ?";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        WorkingConditionsEntity w = new WorkingConditionsEntity();
                        w.setEntity(rs);
                        return w;
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // 全件取得
    public List<WorkingConditionsEntity> findAll() {
        String sql = "SELECT * FROM working_conditions";

        return sqlRepository.execSql(conn -> {
            List<WorkingConditionsEntity> list = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    WorkingConditionsEntity w = new WorkingConditionsEntity();
                    w.setEntity(rs);
                    list.add(w);
                }

                return list;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}

