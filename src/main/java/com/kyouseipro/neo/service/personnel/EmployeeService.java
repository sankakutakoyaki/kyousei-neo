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
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final SqlRepository sqlRepository;
    /**
     * IDからEmployeeを取得
     * @param account
     * @return
     */
    public Entity getEmployeeById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM employees WHERE employee_id = " + id + " AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * アカウントからEmployeeを取得
     * @param account
     * @return
     */
    public Entity getEmployeeByAccount(String account) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM employees WHERE account = '" + account + "' AND NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * すべてのEmployeeを取得
     * @return
     */
    public List<Entity> getEmployeeList() {
        StringBuilder sb = new StringBuilder();
        sb.append(EmployeeListEntity.selectString());
        sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のEmployeeを取得
     * @return
     */
    public List<Entity> getEmployeeListByCategory(int category) {
        StringBuilder sb = new StringBuilder();
        sb.append(EmployeeListEntity.selectString());
        sb.append(" WHERE e.category = " + category + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * Employeeを保存
     * @param employee
     * @return
     */
    public Entity saveEmployee(EmployeeEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getEmployee_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからEmployeeを削除
     * @param ids
     * @return
     */
    public Entity deleteEmployeeByIds(List<SimpleData> list, String userName) {
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
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvEmployeeByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM employees WHERE employee_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        List<Entity> entities = sqlRepository.getEntityList(sqlData);
        return EmployeeEntity.getCsvString(entities);
    }
}
