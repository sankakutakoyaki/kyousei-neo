package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.person.QualificationFilesEntity;
import com.kyouseipro.neo.entity.person.QualificationsEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QualificationsService {
    private final SqlRepository sqlRepository;

    /**
     * IDからQualificationsを取得
     * @param account
     * @return
     */
    public List<IEntity> getQualificationsByEmployeeId(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT q.qualifications_id, q.qualification_master_id, q.number, q.version, q.state");
        sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
        sb.append(", e.employee_id, e.full_name as owner_name, qm.name as qualification_name FROM employees e");
        sb.append(" LEFT OUTER JOIN qualifications q ON q.owner_id = e.employee_id AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" WHERE e.employee_id = " + id + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationsEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * IDからQualificationsを取得
     * @param account
     * @return
     */
    public List<IEntity> getQualificationsByCompanyId(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT q.qualifications_id, q.qualification_master_id, q.number, q.version, q.state");
        sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
        sb.append(", c.company_id, c.name as owner_name, qm.name as qualification_name FROM companies c");
        sb.append(" LEFT OUTER JOIN qualifications q ON q.owner_id = c.company_id AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" WHERE c.company_id = " + id + " AND NOT (c.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationsEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * IDからQualificationsFilesを取得
     * @param account
     * @return
     */
    public List<IEntity> getQualificationsFilesById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM qualifications_files WHERE qualifications_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationFilesEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべてのQualificationsを取得
     * @return
     */
    public List<IEntity> getEmployeeQualificationsList() {
        StringBuilder sb = new StringBuilder();
        sb.append(QualificationsEntity.selectEmployeeStringByStatus());
        // sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationsEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * Qualificationsを保存
     * @param employee
     * @return
     */
    public IEntity saveQualifications(QualificationsEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getQualifications_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからQualificationsを削除
     * @param id
     * @return
     */
    public IEntity deleteQualificationsById(int id, String user_name) {
        QualificationsEntity entity = new QualificationsEntity();
        StringBuilder sb = new StringBuilder();
        sb.append(entity.getDeleteStringById(id, user_name));
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * URLからQualificationsFilesを削除
     * @param ids
     * @return
     */
    public IEntity deleteQualificationsFilesByUrl(String url) {
        QualificationFilesEntity entity = new QualificationFilesEntity();
        StringBuilder sb = new StringBuilder();
        sb.append(entity.getDeleteString(url));
        return sqlRepository.excuteSqlString(sb.toString());
    }
}
