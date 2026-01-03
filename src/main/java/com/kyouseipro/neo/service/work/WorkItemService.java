package com.kyouseipro.neo.service.work;

import java.util.List;
import java.util.Optional;

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
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<WorkItemEntity> getById(int id) {
        return workItemRepository.findById(id);
    }

    /**
     * IDによる取得（CategoryIDで指定）。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public List<WorkItemEntity> getByCategoryId(int id) {
        return workItemRepository.findAllByCategoryId(id);
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<WorkItemEntity> getList() {
        return workItemRepository.findAll();
    }

    /**
     * Entityを登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public int save(WorkItemEntity item, String editor) {
        if (item.getWork_item_id() > 0) {
            return workItemRepository.update(item, editor);
        } else {
            return workItemRepository.insert(item, editor);
        }
        
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list, String userName) {
        return workItemRepository.deleteByIds(list, userName);
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<WorkItemEntity> recycles = workItemRepository.downloadCsvByIds(list);
        return CsvExporter.export(recycles, WorkItemEntity.class);
    }
}
