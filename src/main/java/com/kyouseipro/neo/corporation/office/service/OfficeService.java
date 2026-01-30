package com.kyouseipro.neo.corporation.office.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.corporation.office.entity.OfficeEntity;
import com.kyouseipro.neo.corporation.office.repository.OfficeRepository;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final OfficeRepository officeRepository;

    /**
     * 指定されたIDの支店情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 支店ID
     * @return OfficeEntity または null
     */
    public Optional<OfficeEntity> getById(int id) {
        return officeRepository.findById(id);
    }

    /**
     * 支店情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return
    */
    @HistoryTarget(
        table = HistoryTables.OFFICES,
        action = "保存"
    )
    public int save(OfficeEntity entity, String editor) {
        if (entity.getOfficeId() > 0) {
            return officeRepository.update(entity, editor);
        } else {
            return officeRepository.insert(entity, editor);
        }
    }

    /**
     * IDからOfficeを削除
     * @param ids
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.OFFICES,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return officeRepository.deleteByIds(list, userName);
    }
    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<OfficeEntity> Offices = officeRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(Offices, OfficeEntity.class);
    }
}

