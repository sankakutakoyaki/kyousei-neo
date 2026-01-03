package com.kyouseipro.neo.service.corporation;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
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
    public int save(StaffEntity entity, String editor) {
        if (entity.getStaff_id() > 0) {
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
    public int deleteByIds(List<SimpleData> list, String userName) {
        return staffRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list, String userName) {
        List<StaffEntity> staffs = staffRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(staffs, StaffEntity.class);
    }
}


