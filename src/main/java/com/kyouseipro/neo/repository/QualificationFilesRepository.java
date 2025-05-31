package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.common.QualificationFilesEntity;

public class QualificationFilesRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public QualificationFilesRepository(SqlRepository sqlRepository, GenericRepository genericRepository) {
        this.sqlRepository = sqlRepository;
        this.genericRepository = genericRepository;
    }

    public Integer insertQualificationFiles(QualificationFilesEntity entity, String editor) {
        String sql =
            "DECLARE @InsertedRows TABLE (qualifications_files_id INT);" +
            "INSERT INTO qualifications_files (qualifications_id, file_name, internal_name, folder_name, version, state) " +
            "OUTPUT INSERTED.qualifications_files_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?);" +
            "INSERT INTO qualifications_files_log (qualifications_files_id, editor, process, log_date, " +
            "qualifications_id, file_name, internal_name, folder_name, version, state) " +
            "SELECT qf.qualifications_files_id, ?, 'INSERT', CURRENT_TIMESTAMP, " +
            "qf.qualifications_id, qf.file_name, qf.internal_name, qf.folder_name, qf.version, qf.state " +
            "FROM qualifications_files qf " +
            "INNER JOIN @InsertedRows ir ON qf.qualifications_files_id = ir.qualifications_files_id;" +
            "SELECT qualifications_files_id FROM @InsertedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, entity.getQualifications_id());
                pstmt.setString(2, entity.getFile_name());
                pstmt.setString(3, entity.getInternal_name());
                pstmt.setString(4, entity.getFolder_name());
                pstmt.setInt(5, entity.getVersion());
                pstmt.setInt(6, entity.getState());
                pstmt.setString(7, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("qualifications_files_id");
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

    public Integer updateQualificationFiles(QualificationFilesEntity entity, String editor) {
        String sql =
            "DECLARE @UpdatedRows TABLE (qualifications_files_id INT);" +
            "UPDATE qualifications_files SET " +
            "qualifications_id=?, file_name=?, internal_name=?, folder_name=?, version=?, state=? " +
            "OUTPUT INSERTED.qualifications_files_id INTO @UpdatedRows " +
            "WHERE qualifications_files_id=?;" +
            "INSERT INTO qualifications_files_log (qualifications_files_id, editor, process, log_date, " +
            "qualifications_id, file_name, internal_name, folder_name, version, state) " +
            "SELECT qf.qualifications_files_id, ?, 'UPDATE', CURRENT_TIMESTAMP, " +
            "qf.qualifications_id, qf.file_name, qf.internal_name, qf.folder_name, qf.version, qf.state " +
            "FROM qualifications_files qf " +
            "INNER JOIN @UpdatedRows ur ON qf.qualifications_files_id = ur.qualifications_files_id;" +
            "SELECT qualifications_files_id FROM @UpdatedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, entity.getQualifications_id());
                pstmt.setString(2, entity.getFile_name());
                pstmt.setString(3, entity.getInternal_name());
                pstmt.setString(4, entity.getFolder_name());
                pstmt.setInt(5, entity.getVersion());
                pstmt.setInt(6, entity.getState());
                pstmt.setInt(7, entity.getQualifications_files_id());
                pstmt.setString(8, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("qualifications_files_id");
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

    public QualificationFilesEntity findById(int id) {
        return genericRepository.findOne(
            "SELECT * FROM qualifications_files WHERE qualifications_files_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, id);
                ps.setInt(2, Enums.state.DELETE.getCode());
            },
            QualificationFilesEntity::new
        );
    }

    public List<QualificationFilesEntity> findAllByQualificationsId(int qualificationsId) {
        return genericRepository.findAll(
            "SELECT * FROM qualifications_files WHERE qualifications_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, qualificationsId);
                ps.setInt(2, Enums.state.DELETE.getCode());
            },
            QualificationFilesEntity::new
        );
    }
}

