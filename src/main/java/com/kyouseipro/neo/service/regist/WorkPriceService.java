package com.kyouseipro.neo.service.regist;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.regist.WorkPriceEntity;
import com.kyouseipro.neo.repository.regist.WorkPriceRepository;

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
    public WorkPriceEntity getWorkPriceById(int id) {
        return workPriceRepository.findById(id);
    }

    /**
     * リストを取得
     * @return
     */
    public List<WorkPriceEntity> getList() {
        return workPriceRepository.findAll();
    }

    /**
     * 商品情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveWorkPrice(WorkPriceEntity item, String editor) {
        if (item.getWork_price_id() > 0) {
            return workPriceRepository.updateWorkPrice(item, editor);
        } else {
            return workPriceRepository.insertWorkPrice(item, editor);
        }        
    }

    /**
     * IDからOrderItemを削除
     * @param ids
     * @return
     */
    public Integer deleteWorkPriceByIds(List<SimpleData> list, String userName) {
        return workPriceRepository.deleteWorkPriceByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvOrderByIds(List<SimpleData> list, String userName) {
        List<WorkPriceEntity> workPrices = workPriceRepository.downloadCsvWorkPriceByIds(list, userName);
        return CsvExporter.export(workPrices, WorkPriceEntity.class);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvWorkPriceByIds(List<SimpleData> list) {
        List<WorkPriceEntity> recycles = workPriceRepository.downloadCsvWorkPriceByIds(list);
        return CsvExporter.export(recycles, WorkPriceEntity.class);
    }
}
