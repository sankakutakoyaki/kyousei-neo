package com.kyouseipro.neo.service.work;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.work.WorkItemEntity;
import com.kyouseipro.neo.repository.work.WorkItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkItemService {
    private final WorkItemRepository workItemRepository;
    /**
     * 指定されたIDの受注情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 受注ID
     * @return OrderEntity または null
     */
    public WorkItemEntity getById(int id) {
        return workItemRepository.findById(id);
    }

    /**
     * 
     * @param id
     * @return
     */
    public List<WorkItemEntity> getByCategoryId(int id) {
        return workItemRepository.findAllByCategoryId(id);
    }

    /**
     * リストを取得
     * @return
     */
    public List<WorkItemEntity> getList() {
        return workItemRepository.findAll();
    }

    /**
     * 商品情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer save(WorkItemEntity item, String editor) {
        if (item.getWork_item_id() > 0) {
            return workItemRepository.update(item, editor);
        } else {
            return workItemRepository.insert(item, editor);
        }
        
    }

    /**
     * IDからOrderItemを削除
     * @param ids
     * @return
     */
    public Integer deleteByIds(List<SimpleData> list, String userName) {
        return workItemRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<WorkItemEntity> recycles = workItemRepository.downloadCsvByIds(list);
        return CsvExporter.export(recycles, WorkItemEntity.class);
    }
}
