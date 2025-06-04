package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.repository.corporation.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;

    /**
     * 指定されたIDの営業担当者情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 会社ID
     * @return OfficeEntity または null
     */
    public StaffEntity getStaffById(int id) {
        return staffRepository.findById(id);
    }

    /**
     * 営業担当者情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    public Integer saveStaff(StaffEntity entity, String editor) {
        if (entity.getStaff_id() > 0) {
            return staffRepository.updateStaff(entity, editor);
        } else {
            return staffRepository.insertStaff(entity, editor);
        }
    }

    // /**
    //  * IDからStaffを取得
    //  * @param account
    //  * @return
    //  */
    // public Entity getStaffById(int id) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s");
    //     sb.append(" INNER JOIN companies c ON c.company_id = s.company_id AND NOT (c.state = "  + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" INNER JOIN offices o ON o.office_id = s.office_id AND NOT (o.state = "  + Enums.state.DELETE.getNum() + ")");
    //     sb.append(" WHERE s.staff_id = " + id + " AND NOT (s.state = " + Enums.state.DELETE.getNum() + ");");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new StaffEntity());
    //     return sqlRepository.getEntity(sqlData);
    // }

    // /**
    //  * Staffを保存
    //  * @param staff
    //  * @return
    //  */
    // public Entity saveStaff(StaffEntity entity) {
    //     StringBuilder sb = new StringBuilder();
    //     if (entity.getStaff_id() > 0) {
    //         sb.append(entity.getUpdateString());
    //     } else {
    //         sb.append(entity.getInsertString());
    //     }
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからStaffを削除
    //  * @param ids
    //  * @return
    //  */
    // public Entity deleteStaffByIds(List<SimpleData> list, String userName) {
    //     String ids = Utilities.createSequenceByIds(list);
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("UPDATE staffs SET state = " + Enums.state.DELETE.getNum() + " WHERE staff_id IN(" + ids + ");");
    //     sb.append("DECLARE @ROW_COUNT int;SET @ROW_COUNT = @@ROWCOUNT;");
    //     sb.append("IF @ROW_COUNT > 0 BEGIN ");
    //     sb.append(HistoryEntity.insertString(userName, "staffs", "削除成功", "@ROW_COUNT", ""));
    //     sb.append("SELECT 200 as number, '削除しました' as text; END");
    //     sb.append(" ELSE BEGIN ");
    //     sb.append(HistoryEntity.insertString(userName, "staffs", "削除失敗", "@ROW_COUNT", ""));
    //     sb.append("SELECT 0 as number, '削除できませんでした' as text; END;");
    //     return sqlRepository.excuteSqlString(sb.toString());
    // }

    // /**
    //  * IDからCsv用文字列を取得
    //  * @param ids
    //  * @return
    //  */
    // public String downloadCsvStaffByIds(List<SimpleData> list, String userName) {
    //     String ids = Utilities.createSequenceByIds(list);
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT * FROM staffs WHERE staff_id IN(" + ids + ") AND NOT ( state = " + Enums.state.DELETE.getNum() + " );");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new StaffEntity());
    //     List<Entity> entities = sqlRepository.getEntityList(sqlData);
    //     return StaffEntity.getCsvString(entities);
    // }
}


