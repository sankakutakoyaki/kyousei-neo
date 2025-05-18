package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.EmployeeListEntity;
import com.kyouseipro.neo.entity.record.HistoryEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepositry;

@Service
public class EmployeeService {
    /**
     * IDからEmployeeを取得
     * @param account
     * @return
     */
    public static IEntity getEmployeeById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM employees WHERE employee_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        return SqlRepositry.getEntity(sqlData);
    }

    /**
     * アカウントからEmployeeを取得
     * @param account
     * @return
     */
    public static IEntity getEmployeeByAccount(String account) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM employees WHERE account = '" + account + "' AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
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
    //     sb.append("SELECT e.employee_id, e.code, e.full_name, e.full_name_kana, e.category, e.phone_number");
    //     sb.append(", COALESCE(c.company_name, '') as company_name, COALESCE(o.office_name, '') as office_name");
    //     sb.append(", COALESCE(c.company_name_kana, '') as company_name_kana, COALESCE(o.office_name_kana, '') as office_name_kana FROM employees e");
    //     sb.append(" LEFT OUTER JOIN companies c ON c.company_id = e.company_id AND NOT (c.state = " + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" LEFT OUTER JOIN offices o ON o.office_id = e.office_id AND NOT (o.state = " + Enums.state.DELETE.getNum() + ")");
    //     return sb.toString();
    // }

    /**
     * すべてのEmployeeを取得
     * @return
     */
    public static List<IEntity> getEmployeeList() {
        StringBuilder sb = new StringBuilder();
        sb.append(EmployeeListEntity.selectString());
        sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeListEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のEmployeeを取得
     * @return
     */
    public static List<IEntity> getEmployeeListByCategory(int category) {
        StringBuilder sb = new StringBuilder();
        sb.append(EmployeeListEntity.selectString());
        sb.append(" WHERE e.category = " + category + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeListEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * Employeeを保存
     * @param employee
     * @return
     */
    public static IEntity saveEmployee(EmployeeEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getEmployee_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return SqlRepositry.excuteSqlString(sb.toString());
    }

    /**
     * IDからEmployeeを削除
     * @param ids
     * @return
     */
    public static IEntity deleteEmployeeByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE employees SET state = " + Enums.state.DELETE.getNum() + " WHERE employee_id IN(" + ids + ");");
        sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
        sb.append("IF @ROW_COUNT > 0 BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "employees", "削除成功", "@ROW_COUNT", ""));
        sb.append("SELECT 200 as number, '削除しました' as text; END");
        sb.append(" ELSE BEGIN ");
        sb.append(HistoryEntity.insertString(userName, "employees", "削除失敗", "@ROW_COUNT", ""));
        sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
        return SqlRepositry.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public static String downloadCsvEmployeeByIds(List<SimpleData> list) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM employees WHERE employee_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        List<IEntity> entities = SqlRepositry.getEntityList(sqlData);
        return EmployeeEntity.getCsvString(entities);
    }
}
