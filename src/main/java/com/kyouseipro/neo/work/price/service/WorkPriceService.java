package com.kyouseipro.neo.work.price.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.work.price.entity.WorkPriceEntity;
import com.kyouseipro.neo.work.price.repository.WorkPriceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkPriceService {
    private final WorkPriceRepository workPriceRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<WorkPriceEntity> getById(int id) {
        return workPriceRepository.findById(id);
    }

    /**
     * IDによる取得（CompanyIDで指定）。
     * @param id
     * @return IDから取得したEntityを返す。work_price_idが0の場合は-1からの仮IDを付与する
     */
    public List<WorkPriceEntity> getListByCompanyId(int id) {
        // return workPriceRepository.findAllByCompanyId(id);
        List<WorkPriceEntity> list = workPriceRepository.findAllByCompanyId(id);

        int tempId = -1;
        for (WorkPriceEntity entity : list) {
            if (entity.getWorkPriceId() == 0) {
                entity.setWorkPriceId(tempId);
                tempId--;
            }
        }

        return list;
    }

    /**
     * Entityを登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.WORKPRICIES,
        action = "保存"
    )
    public int save(WorkPriceEntity item, String userName) {
        if (item.getWorkPriceId() > 0) {
            return workPriceRepository.update(item, userName);
        } else {
            return workPriceRepository.insert(item, userName);
        }        
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<WorkPriceEntity> workPrices = workPriceRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(workPrices, WorkPriceEntity.class);
    }
}
