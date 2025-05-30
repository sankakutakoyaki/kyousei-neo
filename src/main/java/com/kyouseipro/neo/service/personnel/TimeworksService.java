package com.kyouseipro.neo.service.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.person.EmployeeListEntity;
import com.kyouseipro.neo.entity.person.TimeworksListEntity;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworksService {
    private final SqlRepository sqlRepository;
    /**
     * 全従業員の今日の勤怠データリスト取得
     * @return List<IEntity>
     */
    public List<Entity> getListOfAllEmployeesToday() {
       StringBuilder sb = new StringBuilder();
        TimeworksListEntity entity = new TimeworksListEntity();
        sb.append(entity.getSelectString());
        LocalDate date = LocalDate.now();
        sb.append(" AND t.work_date = '" + date + "' AND c.category = 0");
        sb.append(" ORDER BY o.office_id");

        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new EmployeeListEntity());
        return sqlRepository.getEntityList(sqlData);
    }
}
