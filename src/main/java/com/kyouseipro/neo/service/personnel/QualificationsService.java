package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.person.QualificationFilesEntity;
import com.kyouseipro.neo.entity.person.QualificationsEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepositry;

@Service
public class QualificationsService {
    /**
     * IDからQualificationsを取得
     * @param account
     * @return
     */
    public static List<IEntity> getQualificationsByEmployeeId(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT q.qualifications_id, q.qualification_master_id, q.number, q.version, q.state");
        sb.append(", COALESCE(q.acquisition_date, '9999-12-31') as acquisition_date, COALESCE(q.expiry_date, '9999-12-31') as expiry_date");
        sb.append(", e.employee_id, e.full_name as employee_name, qm.name as qualification_name FROM employees e");
        sb.append(" LEFT OUTER JOIN qualifications q ON q.employee_id = e.employee_id AND NOT (q.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" LEFT OUTER JOIN qualification_master qm ON qm.qualification_master_id = q.qualification_master_id AND NOT (qm.state = " + Enums.state.DELETE.getNum() + ")");
        sb.append(" WHERE e.employee_id = " + id + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationsEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * IDからQualificationsFilesを取得
     * @param account
     * @return
     */
    public static List<IEntity> getQualificationsFilesById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM qualifications_files WHERE qualifications_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationFilesEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * すべてのQualificationsを取得
     * @return
     */
    public static List<IEntity> getQualificationsList() {
        StringBuilder sb = new StringBuilder();
        sb.append(QualificationsEntity.selectStringByStatus());
        // sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new QualificationsEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * Qualificationsを保存
     * @param employee
     * @return
     */
    public static IEntity saveQualifications(QualificationsEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getQualifications_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return SqlRepositry.excuteSqlString(sb.toString());
    }

    /**
     * IDからQualificationsを削除
     * @param id
     * @return
     */
    public static IEntity deleteQualificationsById(int id, String user_name) {
        QualificationsEntity entity = new QualificationsEntity();
        StringBuilder sb = new StringBuilder();
        sb.append(entity.getDeleteStringById(id, user_name));
        return SqlRepositry.excuteSqlString(sb.toString());
    }

    /**
     * URLからQualificationsFilesを削除
     * @param ids
     * @return
     */
    public static IEntity deleteQualificationsFilesByUrl(String url) {
        QualificationFilesEntity entity = new QualificationFilesEntity();
        StringBuilder sb = new StringBuilder();
        sb.append(entity.getDeleteString(url));
        return SqlRepositry.excuteSqlString(sb.toString());
    }
}
