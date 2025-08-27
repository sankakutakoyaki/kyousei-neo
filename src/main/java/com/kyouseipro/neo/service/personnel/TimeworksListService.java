package com.kyouseipro.neo.service.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.repository.personnel.EmployeeRepository;
import com.kyouseipro.neo.repository.personnel.TimeworksListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworksListService {
    private final TimeworksListRepository timeworksListRepository;

    /**
     * 指定された従業員IDの勤怠情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return TimeworksListEntity または null
     */
    public TimeworksListEntity getEmployeeById(int id) {
        return timeworksListRepository.findById(id);
    }

    /**
     * 今日の前従業員勤怠データを取得する
     * 
     * @return TimeworksListEntityのリスト
     */
    public List<TimeworksListEntity> getTodaysList() {
        LocalDate date = LocalDate.now();
        return timeworksListRepository.findByDate(date);
    }

    // /**
    //  * 全従業員の今日の勤怠データリスト取得
    //  * @return List<IEntity>
    //  */
    // public List<Entity> getListOfAllEmployeesToday() {
    //    StringBuilder sb = new StringBuilder();
    //     TimeworksListEntity entity = new TimeworksListEntity();
    //     sb.append(entity.getSelectString());
    //     LocalDate date = LocalDate.now();
    //     sb.append(" AND t.work_date = '" + date + "' AND c.category = 0");
    //     sb.append(" ORDER BY o.office_id");

    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new EmployeeListEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }
}
