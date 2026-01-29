package com.kyouseipro.neo.service.corporation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.controller.dto.CsvExporter;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
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
    public Optional<StaffEntity> getById(int id) {
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
    @HistoryTarget(
        table = HistoryTables.STAFFS,
        action = "保存"
    )
    public int save(StaffEntity entity, String editor) {
        if (entity.getStaffId() > 0) {
            return staffRepository.update(entity, editor);
        } else {
            return staffRepository.insert(entity, editor);
        }
    }

    /**
     * IDからStaffを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.STAFFS,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return staffRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<StaffEntity> staffs = staffRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(staffs, StaffEntity.class);
    }
}


