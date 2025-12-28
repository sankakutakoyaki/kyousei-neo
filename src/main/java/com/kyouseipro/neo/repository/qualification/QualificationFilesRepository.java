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

    public Integer insert(List<FileUpload> entities, String editor, Integer id) {
        String sql = QualificationFilesSqlBuilder.buildInsert(entities.size());
        return sqlRepository.executeUpdate(
            sql,
            ps -> QualificationFilesParameterBinder.bindInsert(ps, entities, editor, id)
        );
    }

    public Integer delete(String url, String editor) {
        String sql = QualificationFilesSqlBuilder.buildDelete();

        return sqlRepository.executeUpdate(
            sql,
            ps -> QualificationFilesParameterBinder.bindDelete(ps, url, editor)
        );
    }

    // IDによる取得
    public QualificationFilesEntity findById(Integer qualificationFilesId) {
        String sql = QualificationFilesSqlBuilder.buildFindById();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> QualificationFilesParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? QualificationFilesEntityMapper.map(rs) : null,
            qualificationFilesId
        );
    }

    // 資格IDによる取得
    public List<QualificationFilesEntity> findByQualificationsId(Integer qualificationsId) {
        String sql = QualificationFilesSqlBuilder.buildFindAllByQualificationsId();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationFilesParameterBinder.bindFindAllByQualificationsId(ps, qualificationsId),
            QualificationFilesEntityMapper::map
        );
    }
}

