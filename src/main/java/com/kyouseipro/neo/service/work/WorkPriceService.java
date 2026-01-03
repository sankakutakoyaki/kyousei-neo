package com.kyouseipro.neo.service.work;

import java.util.List;
import java.util.Optional;

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
     * @return IDから取得したEntityを返す。
     */
    public List<WorkPriceEntity> getListByCompanyId(int id) {
        return workPriceRepository.findAllByCompanyId(id);
    }

    /**
     * Entityを登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public int save(WorkPriceEntity item, String editor) {
        if (item.getWork_price_id() > 0) {
            return workPriceRepository.update(item, editor);
        } else {
            return workPriceRepository.insert(item, editor);
        }        
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<WorkPriceEntity> workPrices = workPriceRepository.downloadCsvByIds(list);
        return CsvExporter.export(workPrices, WorkPriceEntity.class);
    }
}
