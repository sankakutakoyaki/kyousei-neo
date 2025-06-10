package com.kyouseipro.neo.service.qualification;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
import com.kyouseipro.neo.entity.qualification.QualificationsEntity;
import com.kyouseipro.neo.repository.qualification.QualificationFilesRepository;
import com.kyouseipro.neo.repository.qualification.QualificationsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QualificationsService {
    private final QualificationsRepository qualificationsRepository;

    /**
     * 指定されたIDの資格情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 資格ID
     * @return QualificationsEntity または null
     */
    public QualificationsEntity getQualificationsById(Integer id) {
        return qualificationsRepository.findById(id);
    }

    /**
     * 指定されたEmployeeIDの資格情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 資格ID
     * @return QualificationsEntityのリスト または null
     */
    public List<QualificationsEntity> getQualificationsByEmployeeId(Integer id) {
        return qualificationsRepository.findByEmployeeId(id);
    }

    /**
     * 指定されたCompanyIDの資格情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 資格ID
     * @return QualificationsEntityのリスト または null
     */
    public List<QualificationsEntity> getQualificationsByCompanyId(Integer id) {
        return qualificationsRepository.findByCompanyId(id);
    }

    /**
     * 資格情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    public Integer saveQualifications(QualificationsEntity entity, String editor) {
        if (entity.getQualifications_id() > 0) {
            return qualificationsRepository.updateQualifications(entity, editor);
        } else {
            return qualificationsRepository.insertQualifications(entity, editor);
        }
    }

    /**
     * IDからQualificationsを削除
     * @param id
     * @return
     */
    public Integer deleteQualificationsById(Integer id, String userName) {
        return qualificationsRepository.deleteQualifications(id, userName);
    }

    /**
     * IDSからQualificationsを削除
     * @param ids
     * @return
     */
    public Integer deleteQualificationsByIds(List<SimpleData> list, String userName) {
        return qualificationsRepository.deleteQualificationsByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvQualificationsByIds(List<SimpleData> list, String userName) {
        List<QualificationsEntity> qualifications = qualificationsRepository.downloadCsvQualificationsByIds(list, userName);
        return CsvExporter.export(qualifications, QualificationsEntity.class);
    }

    /**
     * すべての資格情報を取得
     * @return
     */
    public List<QualificationsEntity> getEmployeeQualificationsList() {
        return qualificationsRepository.findAllForEmployee();
    }

    /**
     * すべての許認可情報を取得
     * @return
     */
    public List<QualificationsEntity> getCompanyQualificationsList() {
        return qualificationsRepository.findAllForCompany();
    }


    // /**
    //  * IDからQualificationsを取得
    //  * @param account
    //  * @return
    //  */
    // public List<Entity> getQualificationsByEmployeeId(int id) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT q.qualifications_id, q.qualification_master_id, q.number, q.version, q.state");
    //     sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
    //     sb.append(", e.employee_id, e.full_name as owner_name, qm.name as qualification_name FROM employees e");
    //     sb.append(" LEFT OUTER JOIN qualifications q ON q.owner_id = e.employee_id AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" WHERE e.employee_id = " + id + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new QualificationsEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * IDからQualificationsを取得
    //  * @param account
    //  * @return
    //  */
    // public List<Entity> getQualificationsByCompanyId(int id) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT q.qualifications_id, q.qualification_master_id, q.number, q.version, q.state");
    //     sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
    //     sb.append(", c.company_id, c.name as owner_name, qm.name as qualification_name FROM companies c");
    //     sb.append(" LEFT OUTER JOIN qualifications q ON q.owner_id = c.company_id AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" WHERE c.company_id = " + id + " AND NOT (c.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new QualificationsEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * IDからQualificationsFilesを取得
    //  * @param account
    //  * @return
    //  */
    // public List<Entity> getQualificationsFilesById(int id) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT * FROM qualifications_files WHERE qualifications_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new QualificationFilesEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * すべてのQualifications(Employee)を取得
    //  * @return
    //  */
    // public List<Entity> getEmployeeQualificationsList() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(QualificationsEntity.selectEmployeeStringByStatus());
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new QualificationsEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * すべてのQualifications(Company)を取得
    //  * @return
    //  */
    // public List<Entity> getCompanyQualificationsList() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(QualificationsEntity.selectCompanyStringByStatus());
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new QualificationsEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * Qualificationsを保存
    //  * @param employee
    //  * @return
    //  */
    // public Entity saveQualifications(QualificationsEntity entity) {
    //     StringBuilder sb = new StringBuilder();
    //     if (entity.getQualifications_id() > 0) {
    //         sb.append(entity.getUpdateString());
    //     } else {
    //         sb.append(entity.getInsertString());
    //     }
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからQualificationsを削除
    //  * @param id
    //  * @return
    //  */
    // public Entity deleteQualificationsById(int id, String user_name) {
    //     QualificationsEntity entity = new QualificationsEntity();
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(entity.getDeleteStringById(id, user_name));
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * URLからQualificationsFilesを削除
    //  * @param ids
    //  * @return
    //  */
    // public Entity deleteQualificationsFilesByUrl(String url) {
    //     QualificationFilesEntity entity = new QualificationFilesEntity();
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(entity.getDeleteString(url));
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }
}
