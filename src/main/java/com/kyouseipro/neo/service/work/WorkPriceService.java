package com.kyouseipro.neo.service.work;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.work.WorkPriceEntity;
import com.kyouseipro.neo.repository.work.WorkPriceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkPriceService {
    private final WorkPriceRepository workPriceRepository;
    /**
     * 指定されたIDの受注情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 受注ID
     * @return OrderEntity または null
     */
    public WorkPriceEntity getById(int id) {
        return workPriceRepository.findById(id);
    }

    /**
     * 荷主IDで指定した料金表を取得
     * @param id
     * @return
     */
    public List<WorkPriceEntity> getListByCompanyId(int id) {
        return workPriceRepository.findAllByCompanyId(id);
    }

    /**
     * 商品情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer save(WorkPriceEntity item, String editor) {
        if (item.getWork_price_id() > 0) {
            return workPriceRepository.update(item, editor);
        } else {
            return workPriceRepository.insert(item, editor);
        }        
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<WorkPriceEntity> workPrices = workPriceRepository.downloadCsvByIds(list);
        return CsvExporter.export(workPrices, WorkPriceEntity.class);
    }
}
