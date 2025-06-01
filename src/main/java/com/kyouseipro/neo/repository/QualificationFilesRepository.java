package com.kyouseipro.neo.repository;

import java.sql.*;
import java.util.List;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.common.QualificationFilesEntity;
import com.kyouseipro.neo.query.parameter.QualificationFilesParameterBinder;
import com.kyouseipro.neo.query.sql.QualificationFilesSqlBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QualificationFilesRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public Integer insertQualificationFiles(QualificationFilesEntity entity, String editor) {
        String sql = QualificationFilesSqlBuilder.buildInsertQualificationFilesSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationFilesParameterBinder.bindInsertQualificationFilesParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_files_id") : null,
            entity
        );
    }

    public Integer updateQualificationFiles(QualificationFilesEntity entity, String editor) {
        String sql = QualificationFilesSqlBuilder.buildUpdateQualificationFilesSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationFilesParameterBinder.bindUpdateQualificationFilesParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_files_id") : null,
            entity
        );
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

