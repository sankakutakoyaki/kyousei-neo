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
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkingConditionsService {
    private final SqlRepository sqlRepository;
    /**
     * IDからWorkingConditionsを取得
     * @param account
     * @return
     */
    public IEntity getWorkingConditionsById(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsEntity.selectString());
        sb.append(" WHERE e.employee_id = " + id + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * アカウントからWorkingConditionsを取得
     * @param account
     * @return
     */
    public IEntity getWorkingConditionsByAccount(String account) {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsEntity.selectString());
        sb.append(" WHERE e.account = '" + account + "' AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * すべてのWorkingConditonsを取得
     * @return
     */
    public List<IEntity> getWorkingConditionsList() {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsListEntity.selectString());
        sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * カテゴリー別のWorkingConditionsを取得
     * @return
     */
    public List<IEntity> getWorkingConditionsListByCategory(int category) {
        StringBuilder sb = new StringBuilder();
        sb.append(WorkingConditionsListEntity.selectString());
        sb.append(" WHERE e.category = " + category + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsListEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * WorkingConditionsを保存
     * @param working_conditions
     * @return
     */
    public IEntity saveWorkingConditions(WorkingConditionsEntity entity) {
        StringBuilder sb = new StringBuilder();
        if (entity.getWorking_conditions_id() > 0) {
            sb.append(entity.getUpdateString());
        } else {
            sb.append(entity.getInsertString());
        }
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからWorkingConditionsを削除
     * @param ids
     * @return
     */
    public IEntity deleteWorkingConditionsByIds(List<SimpleData> list, String userName) {
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
        return sqlRepository.excuteSqlString(sb.toString());
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvWorkingConditionsByIds(List<SimpleData> list, String userName) {
        String ids = Utilities.createSequenceByIds(list);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM working_conditions WHERE working_conditions_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new WorkingConditionsEntity());
        List<IEntity> entities = sqlRepository.getEntityList(sqlData);
        return WorkingConditionsEntity.getCsvString(entities);
    }
}
