package com.kyouseipro.neo.service.corporation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.repository.corporation.StaffListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffListService {
    private final StaffListRepository staffListRepository;
    
    /**
     * すべてのStaffを取得
     * @return
     */
    public List<StaffListEntity> getStaffList() {
        return staffListRepository.findAll();
        // StringBuilder sb = new StringBuilder();
        // sb.append("SELECT s.*, c.name as company_name, o.name as office_name FROM staffs s");
        // sb.append(" INNER JOIN companies c ON c.company_id = s.company_id AND NOT (c.state = "  + Enums.state.DELETE.getNum() + ")");
        // sb.append(" INNER JOIN offices o ON o.office_id = s.office_id AND NOT (o.state = "  + Enums.state.DELETE.getNum() + ")");
        // sb.append(" WHERE NOT (s.state = " + Enums.state.DELETE.getNum() + ");");
        // SqlData sqlData = new SqlData();
        // sqlData.setData(sb.toString(), new StaffEntity());
        // return sqlRepository.getEntityList(sqlData);
    }
}
