package com.kyouseipro.neo.repository.qualification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.mapper.data.SimpleDataMapper;
import com.kyouseipro.neo.mapper.qualification.QualificationsEntityMapper;
import com.kyouseipro.neo.query.parameter.qualification.QualificationsParameterBinder;
import com.kyouseipro.neo.query.sql.qualification.QualificationsSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;
import com.kyouseipro.neo.repository.personnel.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QualificationsRepository {
    private final SqlRepository sqlRepository;
    private final EmployeeRepository employeeRepository;

    // INSERT
    public Integer insert(QualificationsEntity q, String editor) {
        String sql = QualificationsSqlBuilder.buildInsert();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindInsert(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            q
        );
    }

    // UPDATE
    public Integer update(QualificationsEntity q, String editor) {
        String sql = QualificationsSqlBuilder.buildUpdate();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindUpdate(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            q
        );
    }

    // DELETE
    public Integer delete(int id, String editor) {
        String sql = QualificationsSqlBuilder.buildDelete();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindDelete(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            id
        );
    }

    public Integer deleteByIds(List<SimpleData> ids, String editor) {
        List<Integer> qualificationsIds = Utilities.createSequenceByIds(ids);
        String sql = QualificationsSqlBuilder.buildDeleteByIds(qualificationsIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> QualificationsParameterBinder.bindDeleteForIds(ps, qualificationsIds, editor)
        );
    }

    public List<QualificationsEntity> downloadCsvByIds(List<SimpleData> ids, String editor) {
        List<Integer> qualificationsIds = Utilities.createSequenceByIds(ids);
        String sql = QualificationsSqlBuilder.buildDownloadCsvByIds(qualificationsIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindDownloadCsvForIds(ps, qualificationsIds),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる資格情報取得
    public List<QualificationsEntity> findAllByEmployeeId(int employeeId) {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployeeId();
            EmployeeEntity entity = employeeRepository.findById(employeeId);
            int id = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllByEmployeeId(ps, id),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる許認可情報取得
    public List<QualificationsEntity> findAllByCompanyId(int companyId) {
        String sql = QualificationsSqlBuilder.buildFindAllByCompanyId();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllByCompanyId(ps, companyId),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAllByEmployeeStatus() {
        String sql = QualificationsSqlBuilder.buildFindAllByEmployeeStatus();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllByEmployeeStatus(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAllByCompanyStatus() {
        String sql = QualificationsSqlBuilder.buildFindAllByCompanyStatus();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllByCompanyStatus(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllByQualificationMasterCombo() {
        String sql = QualificationsSqlBuilder.buildFindAllByQualificationMasterCombo();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllByQualificationMasterCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllByLicenseMasterCombo() {
        String sql = QualificationsSqlBuilder.buildFindAllByLicenseMasterCombo();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllByLicenseMasterCombo(ps, null),
            SimpleDataMapper::map
        );
    }
}

