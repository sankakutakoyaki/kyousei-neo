package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.WorkingConditionsEntity;
import com.kyouseipro.neo.entity.person.WorkingConditionsListEntity;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepositry;

@Service
public class WorkingConditionsService {
    /**
     * IDからWorkingConditionsを取得
     * @param account
     * @return
     */
    public static IEntity getWorkingConditionsById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsEntity.selectString());
        sb.append(" WHERE e.employee_id = " + id + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsEntity());
        return SqlRepositry.getEntity(sqlData);
    }

    /**
     * アカウントからWorkingConditionsを取得
     * @param account
     * @return
     */
    public static IEntity getWorkingConditionsByAccount(String account) {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsEntity.selectString());
        sb.append(" WHERE e.account = '" + account + "' AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        return SqlRepositry.getEntity(sqlData);
    }

    // /**
    //  * リストセレクト用基本文字列
    //  * @return
    //  */
    // private static String selectString() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT e.employee_id, e.full_name, e.full_name_kana, e.code, e.category");
    //     sb.append(", w.working_conditions_id, w.payment_method, w.pay_type, w.base_salary, w.trans_cost, w.version, w.state");
    //     sb.append(", ISNULL(NULLIF(w.basic_start_time, ''), '00:00:00') as basic_start_time, ISNULL(NULLIF(w.basic_end_time, ''), '00:00:00') as basic_end_time");
    //     sb.append(", ISNULL(NULLIF(o.office_name, ''), '登録なし') as office_name FROM employees e");
    //     sb.append(" LEFT OUTER JOIN working_conditions w ON w.employee_id = e.employee_id AND NOT (w.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = " + Enums.state.DELETE.getNum() + ")");
    //     return sb.toString();
    // }

    /**
     * すべてのWorkingConditonsを取得
     * @return
     */
    public static List<IEntity> getWorkingConditionsList() {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsListEntity.selectString());
        sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsListEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のWorkingConditionsを取得
     * @return
     */
    public static List<IEntity> getWorkingConditionsListByCategory(int category) {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsListEntity.selectString());
        sb.append(" WHERE e.category = " + category + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsListEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * WorkingConditionsを保存
     * @param working_conditions
     * @return
     */
    public static IEntity saveWorkingConditions(WorkingConditionsEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getWorking_conditions_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return SqlRepositry.excuteSqlString(sb.toString());
    }

    /**
     * IDからWorkingConditionsを削除
     * @param ids
     * @return
     */
    public static IEntity deleteWorkingConditionsByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE working_conditions SET state = " + Enums.state.DELETE.getNum() + " WHERE working_conditions_id IN(" + ids + ");");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        sb.append("IF @ROW_COUNT > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "working_conditions", "削除成功", "@ROW_COUNT", ""));
        sb.append("SELECT '" + userName + "' as user, 'working_conditions' as table, 200 as number, '削除しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "working_conditions", "削除失敗", "@ROW_COUNT", ""));
        sb.append("SELECT '" + userName + "' as user, 'working_conditions' as table, 0 as number, '削除できませんでした' as text; END;");
        return SqlRepositry.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public static String downloadCsvWorkingConditionsByIds(List<SimpleData> list) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM working_conditions WHERE working_conditions_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsEntity());
        List<IEntity> entities = SqlRepositry.getEntityList(sqlData);
        return WorkingConditionsEntity.getCsvString(entities);
    }
}
