package com.kyouseipro.neo.repository.qualification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.mapper.qualification.QualificationFilesEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationFilesParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationFilesSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationFilesRepository {
    private final SqlRepository sqlRepository;

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

    // IDによる取得
    public QualificationFilesEntity findById(int qualificationFilesId) {
        String sql = QualificationFilesSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> QualificationFilesParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? QualificationFilesEntityMapper.map(rs) : null,
            qualificationFilesId
        );
    }

    // 資格IDによる取得
    public List<QualificationFilesEntity> findByQualificationsId() {
        String sql = QualificationFilesSqlBuilder.buildFindAllByQualificationsIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationFilesParameterBinder.bindFindAllByQualificationsId(ps, null),
            QualificationFilesEntityMapper::map
        );
    }
}

