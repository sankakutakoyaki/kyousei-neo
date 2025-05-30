package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.common.QualificationsEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationsRepository {

    private final SqlRepository sqlRepository;

    // INSERT
    public Integer insert(QualificationsEntity q, String editor) {
        String sql =
            "DECLARE @Inserted TABLE (qualifications_id INT);" +
            "INSERT INTO qualifications (owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "OUTPUT INSERTED.qualifications_id INTO @Inserted " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO qualifications_log (qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "SELECT q.qualifications_id, ?, 'INSERT', CURRENT_TIMESTAMP, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.version, q.state " +
            "FROM qualifications q INNER JOIN @Inserted i ON q.qualifications_id = i.qualifications_id;" +
            "SELECT qualifications_id FROM @Inserted;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, q.getOwner_id());
                pstmt.setInt(2, q.getQualification_master_id());
                pstmt.setString(3, q.getNumber());
                pstmt.setDate(4, Date.valueOf(q.getAcquisition_date()));
                pstmt.setDate(5, Date.valueOf(q.getExpiry_date()));
                pstmt.setInt(6, q.getVersion());
                pstmt.setInt(7, q.getState());

                pstmt.setString(8, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("qualifications_id");
                        }
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
    public Integer update(QualificationsEntity q, String editor) {
        String sql =
            "DECLARE @Updated TABLE (qualifications_id INT);" +
            "UPDATE qualifications SET owner_id=?, qualification_master_id=?, number=?, acquisition_date=?, expiry_date=?, version=?, state=? " +
            "OUTPUT INSERTED.qualifications_id INTO @Updated " +
            "WHERE qualifications_id=?;" +
            "INSERT INTO qualifications_log (qualifications_id, editor, process, log_date, owner_id, qualification_master_id, number, acquisition_date, expiry_date, version, state) " +
            "SELECT q.qualifications_id, ?, 'UPDATE', CURRENT_TIMESTAMP, q.owner_id, q.qualification_master_id, q.number, q.acquisition_date, q.expiry_date, q.version, q.state " +
            "FROM qualifications q INNER JOIN @Updated u ON q.qualifications_id = u.qualifications_id;" +
            "SELECT qualifications_id FROM @Updated;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, q.getOwner_id());
                pstmt.setInt(2, q.getQualification_master_id());
                pstmt.setString(3, q.getNumber());
                pstmt.setDate(4, Date.valueOf(q.getAcquisition_date()));
                pstmt.setDate(5, Date.valueOf(q.getExpiry_date()));
                pstmt.setInt(6, q.getVersion());
                pstmt.setInt(7, q.getState());

                pstmt.setInt(8, q.getQualifications_id());

                pstmt.setString(9, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("qualifications_id");
                        }
                    }
                }

                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // IDによる検索
    public QualificationsEntity findById(int id) {
        String sql = "SELECT * FROM qualifications WHERE qualifications_id = ?";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        QualificationsEntity q = new QualificationsEntity();
                        q.setEntity(rs);
                        return q;
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
    public List<QualificationsEntity> findAll() {
        String sql = "SELECT * FROM qualifications";

        return sqlRepository.execSql(conn -> {
            List<QualificationsEntity> list = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    QualificationsEntity q = new QualificationsEntity();
                    q.setEntity(rs);
                    list.add(q);
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}

