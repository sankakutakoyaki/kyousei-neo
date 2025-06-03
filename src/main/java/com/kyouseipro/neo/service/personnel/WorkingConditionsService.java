package com.kyouseipro.neo.service.personnel;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.repository.personnel.WorkingConditionsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkingConditionsService {
    private final WorkingConditionsRepository workingConditionsRepository;

    /**
     * 指定されたIDの労働条件情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 労働条件ID
     * @return WorkingConditionsEntity または null
     */
    public WorkingConditionsEntity getWorkingConditionsById(int id) {
        return workingConditionsRepository.findById(id);
    }

    /**
     * 労働条件情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    public Integer saveWorkingConditions(WorkingConditionsEntity entity, String editor) {
        if (entity.getWorking_conditions_id() > 0) {
            return workingConditionsRepository.updateWorkingConditions(entity, editor);
        } else {
            return workingConditionsRepository.insertWorkingConditions(entity, editor);
        }
    }

    // /**
    //  * IDからWorkingConditionsを取得
    //  * @param account
    //  * @return
    //  */
    // public Entity getWorkingConditionsById(int id) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(WorkingConditionsEntity.selectString());
    //     sb.append(" WHERE e.employee_id = " + id + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new WorkingConditionsEntity());
    //     return sqlRepository.getEntity(sqlData);
    // }

    // /**
    //  * アカウントからWorkingConditionsを取得
    //  * @param account
    //  * @return
    //  */
    // public Entity getWorkingConditionsByAccount(String account) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(WorkingConditionsEntity.selectString());
    //     sb.append(" WHERE e.account = '" + account + "' AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new EmployeeEntity());
    //     return sqlRepository.getEntity(sqlData);
    // }

    // /**
    //  * すべてのWorkingConditonsを取得
    //  * @return
    //  */
    // public List<Entity> getWorkingConditionsList() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(WorkingConditionsListEntity.selectString());
    //     sb.append(" WHERE NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new WorkingConditionsListEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * カテゴリー別のWorkingConditionsを取得
    //  * @return
    //  */
    // public List<Entity> getWorkingConditionsListByCategory(int category) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append(WorkingConditionsListEntity.selectString());
    //     sb.append(" WHERE e.category = " + category + " AND NOT (e.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new WorkingConditionsListEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * WorkingConditionsを保存
    //  * @param working_conditions
    //  * @return
    //  */
    // public Entity saveWorkingConditions(WorkingConditionsEntity entity) {
    //     StringBuilder sb = new StringBuilder();
    //     if (entity.getWorking_conditions_id() > 0) {
    //         sb.append(entity.getUpdateString());
    //     } else {
    //         sb.append(entity.getInsertString());
    //     }
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからWorkingConditionsを削除
    //  * @param ids
    //  * @return
    //  */
    // public Entity deleteWorkingConditionsByIds(List<SimpleData> list, String userName) {
    //     String ids = Utilities.createSequenceByIds(list);
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("UPDATE working_conditions SET state = " + Enums.state.DELETE.getNum() + " WHERE working_conditions_id IN(" + ids + ");");
    //     sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
    //     sb.append("IF @ROW_COUNT > 0 BEGIN ");
    //     sb.append(HistoryEntity.insertString(userName, "working_conditions", "削除成功", "@ROW_COUNT", ""));
    //     sb.append("SELECT '" + userName + "' as user, 'working_conditions' as table, 200 as number, '削除しました' as text; END");
    //     sb.append(" ELSE BEGIN ");
    //     sb.append(HistoryEntity.insertString(userName, "working_conditions", "削除失敗", "@ROW_COUNT", ""));
    //     sb.append("SELECT '" + userName + "' as user, 'working_conditions' as table, 0 as number, '削除できませんでした' as text; END;");
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからCsv用文字列を取得
    //  * @param ids
    //  * @return
    //  */
    // public String downloadCsvWorkingConditionsByIds(List<SimpleData> list, String userName) {
    //     String ids = Utilities.createSequenceByIds(list);
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT * FROM working_conditions WHERE working_conditions_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new WorkingConditionsEntity());
    //     List<Entity> entities = sqlRepository.getEntityList(sqlData);
    //     return WorkingConditionsEntity.getCsvString(entities);
    // }
}
