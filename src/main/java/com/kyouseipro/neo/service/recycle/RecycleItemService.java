package com.kyouseipro.neo.service.recycle;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;
import com.kyouseipro.neo.repository.recycle.RecycleItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleItemService {
    private final RecycleItemRepository recycleItemRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<RecycleItemEntity> getById(int id) {
        return recycleItemRepository.findById(id);
    }

    /**
     * Codeによる取得。
     * @param code
     * @return Codeから取得したEntityを返す。
     */
    public Optional<RecycleItemEntity> getByCode(int code) {
        return recycleItemRepository.findByCode(code);

    }

    /**
     * Entityを登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public int save(RecycleItemEntity entity) {
        if (entity.getRecycle_item_id() > 0) {
            return recycleItemRepository.update(entity);
        } else {
            return recycleItemRepository.insert(entity);
        }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    public int deleteByIds(List<SimpleData> list) {
        return recycleItemRepository.deleteByIds(list);
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<RecycleItemEntity> recycles = recycleItemRepository.downloadCsvByIds(list);
        return CsvExporter.export(recycles, RecycleItemEntity.class);
    }
}
