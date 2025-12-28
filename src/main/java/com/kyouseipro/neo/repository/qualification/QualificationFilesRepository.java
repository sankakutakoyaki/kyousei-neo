package com.kyouseipro.neo.repository.qualification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.interfaceis.FileUpload;
import com.kyouseipro.neo.mapper.qualification.QualificationFilesEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationFilesParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationFilesSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationFilesRepository {
    private final SqlRepository sqlRepository;

    public Integer insertQualificationFiles(List<FileUpload> entities, String editor, Integer id) {
        String sql = QualificationFilesSqlBuilder.buildInsertQualificationFilesSql(entities.size());
        return sqlRepository.executeUpdate(
            sql,
            ps -> QualificationFilesParameterBinder.bindInsertQualificationFilesParameters(ps, entities, editor, id)
        );
        // return sqlRepository.execute(
        //     sql,
        //     (pstmt, ent) -> QualificationFilesParameterBinder.bindInsertQualificationFilesParameters(pstmt, ent, editor),
        //     rs -> rs.next() ? rs.getInt("qualifications_files_id") : null,
        //     entities
        // );
    }

    // public Integer updateQualificationFiles(QualificationFilesEntity entity, String editor) {
    //     String sql = QualificationFilesSqlBuilder.buildUpdateQualificationFilesSql();

    //     return sqlRepository.execute(
    //         sql,
    //         (pstmt, ent) -> QualificationFilesParameterBinder.bindUpdateQualificationFilesParameters(pstmt, ent, editor),
    //         rs -> rs.next() ? rs.getInt("qualifications_files_id") : null,
    //         entity
    //     );
    // }

    public Integer deleteQualificationFilesByUrl(String url, String editor) {
        String sql = QualificationFilesSqlBuilder.buildDeleteQualificationFilesSql();

        return sqlRepository.executeUpdate(
            sql,
            ps -> QualificationFilesParameterBinder.bindDeleteQualificationFilesParameters(ps, url, editor)
        );
    }

    // IDによる取得
    public QualificationFilesEntity findByQualificationsFilesId(Integer qualificationFilesId) {
        String sql = QualificationFilesSqlBuilder.buildFindByQualificationsFilesIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> QualificationFilesParameterBinder.bindFindByQualificationsFIlesId(pstmt, comp),
            rs -> rs.next() ? QualificationFilesEntityMapper.map(rs) : null,
            qualificationFilesId
        );
    }

    // 資格IDによる取得
    public List<QualificationFilesEntity> findByQualificationsId(Integer qualificationsId) {
        String sql = QualificationFilesSqlBuilder.buildFindAllByQualificationsIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationFilesParameterBinder.bindFindAllByQualificationsId(ps, qualificationsId),
            QualificationFilesEntityMapper::map
        );
    }
}

