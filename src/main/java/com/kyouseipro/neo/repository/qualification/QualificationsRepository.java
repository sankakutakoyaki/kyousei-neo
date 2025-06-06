package com.kyouseipro.neo.repository.qualification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
import com.kyouseipro.neo.mapper.qualification.QualificationsEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationsParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationsSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationsRepository {
    private final SqlRepository sqlRepository;

    // INSERT
    public Integer insertQualifications(QualificationsEntity q, String editor) {
        String sql = QualificationsSqlBuilder.buildInsertQualificationsSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindInsertQualificationsParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            q
        );
    }

    // UPDATE
    public Integer updateQualifications(QualificationsEntity q, String editor) {
        String sql = QualificationsSqlBuilder.buildUpdateQualificationsSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindUpdateQualificationsParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            q
        );
    }

    // DELETE
    public int deleteQualifications(int id, String editor) {
        String sql = QualificationsSqlBuilder.buildUpdateQualificationsSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindDeleteQualificationsParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            id
        );
    }

    public int deleteQualificationsByIds(List<SimpleData> ids, String editor) {
        List<Integer> qualificationsIds = Utilities.createSequenceByIds(ids);
        String sql = QualificationsSqlBuilder.buildDeleteQualificationsForIdsSql(qualificationsIds.size());

        int result = sqlRepository.executeUpdate(
            sql,
            ps -> QualificationsParameterBinder.bindDeleteForIds(ps, qualificationsIds)
        );

        return result; // 成功件数。0なら削除なし
    }

    public List<QualificationsEntity> downloadCsvQualificationsByIds(List<SimpleData> ids, String editor) {
        List<Integer> qualificationsIds = Utilities.createSequenceByIds(ids);
        String sql = QualificationsSqlBuilder.buildDownloadCsvQualificationsForIdsSql(qualificationsIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAll(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる取得
    public QualificationsEntity findById(int qualificationsId) {
        String sql = QualificationsSqlBuilder.buildFindByIdSql();

        return sqlRepository.execute(
            sql,
            (pstmt, comp) -> QualificationsParameterBinder.bindFindById(pstmt, comp),
            rs -> rs.next() ? QualificationsEntityMapper.map(rs) : null,
            qualificationsId
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAllForEmployee() {
        String sql = QualificationsSqlBuilder.buildFindAllEmployeeSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAll(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAllForCompany() {
        String sql = QualificationsSqlBuilder.buildFindAllCompanySql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAll(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllCombo() {
        String sql = QualificationsSqlBuilder.buildFindAllComboQualificationsSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllCombo(ps, null),
            SimpleDataMapper::map
        );
    }
}

