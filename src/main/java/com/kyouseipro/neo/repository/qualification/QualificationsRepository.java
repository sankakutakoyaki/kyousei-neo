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
    public Integer deleteQualifications(int id, String editor) {
        String sql = QualificationsSqlBuilder.buildDeleteQualificationsSql();

        return sqlRepository.execute(
            sql,
            (pstmt, ent) -> QualificationsParameterBinder.bindDeleteQualificationsParameters(pstmt, ent, editor),
            rs -> rs.next() ? rs.getInt("qualifications_id") : null,
            id
        );
    }

    public Integer deleteQualificationsByIds(List<SimpleData> ids, String editor) {
        List<Integer> qualificationsIds = Utilities.createSequenceByIds(ids);
        String sql = QualificationsSqlBuilder.buildDeleteQualificationsForIdsSql(qualificationsIds.size());

        return sqlRepository.executeUpdate(
            sql,
            ps -> QualificationsParameterBinder.bindDeleteForIds(ps, qualificationsIds, editor)
        );
    }

    public List<QualificationsEntity> downloadCsvQualificationsByIds(List<SimpleData> ids, String editor) {
        List<Integer> qualificationsIds = Utilities.createSequenceByIds(ids);
        String sql = QualificationsSqlBuilder.buildDownloadCsvQualificationsForIdsSql(qualificationsIds.size());

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindDownloadCsvForIds(ps, qualificationsIds),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // // IDによる取得
    // public QualificationsEntity findByIdForEmployee(int qualificationsId) {
    //     String sql = QualificationsSqlBuilder.buildFindByIdForEmployeeSql();

    //     return sqlRepository.execute(
    //         sql,
    //         (pstmt, comp) -> QualificationsParameterBinder.bindFindByIdForEmployee(pstmt, comp),
    //         rs -> rs.next() ? QualificationsEntityMapper.map(rs) : null,
    //         qualificationsId
    //     );
    // }

    // // IDによる取得
    // public QualificationsEntity findByIdForCompany(int qualificationsId) {
    //     String sql = QualificationsSqlBuilder.buildFindByIdForCompanySql();

    //     return sqlRepository.execute(
    //         sql,
    //         (pstmt, comp) -> QualificationsParameterBinder.bindFindByIdForCompany(pstmt, comp),
    //         rs -> rs.next() ? QualificationsEntityMapper.map(rs) : null,
    //         qualificationsId
    //     );
    // }

    // IDによる資格情報取得
    public List<QualificationsEntity> findByIdForEmployee(int employeeId) {
        String sql = QualificationsSqlBuilder.buildFindAllEmployeeIdSql();
            EmployeeEntity entity = employeeRepository.findById(employeeId);
            int id = entity.getEmployee_id();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindByIdForEmployee(ps, id),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // IDによる許認可情報取得
    public List<QualificationsEntity> findByIdForCompany(int companyId) {
        String sql = QualificationsSqlBuilder.buildFindAllCompanyIdSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindByIdForCompany(ps, companyId),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAllForEmployee() {
        String sql = QualificationsSqlBuilder.buildFindAllEmployeeStatusSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllEmployeeStatus(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // 全件取得
    public List<QualificationsEntity> findAllForCompany() {
        String sql = QualificationsSqlBuilder.buildFindAllCompanyStatusSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllCompanyStatus(ps, null),
            QualificationsEntityMapper::map // ← ここで ResultSet を map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllCombo() {
        String sql = QualificationsSqlBuilder.buildFindAllComboQualificationMasterSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllCombo(ps, null),
            SimpleDataMapper::map
        );
    }

    // コンボボックス用リスト取得
    public List<SimpleData> findAllComboByLicense() {
        String sql = QualificationsSqlBuilder.buildFindAllComboLicenseMasterSql();

        return sqlRepository.findAll(
            sql,
            ps -> QualificationsParameterBinder.bindFindAllCombo(ps, null),
            SimpleDataMapper::map
        );
    }
}

