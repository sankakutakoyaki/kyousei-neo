package com.kyouseipro.neo.service.recycle;

import java.util.List;

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
     * 指定されたIDのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id リサイクルID
     * @return RecycleEntity または null
     */
    public RecycleItemEntity getById(int id) {
        return recycleItemRepository.findById(id);
    }

    /**
     * 指定されたCodeのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param code
     * @return RecycleEntity または null
     */
    public RecycleItemEntity getByCode(int code) {
        return recycleItemRepository.findByCode(code);

    }

    /**
     * リサイクル情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer save(RecycleItemEntity entity) {
        if (entity.getRecycle_item_id() > 0) {
            return recycleItemRepository.update(entity);
        } else {
            return recycleItemRepository.insert(entity);
        }
    }

    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteByIds(List<SimpleData> list) {
        return recycleItemRepository.deleteByIds(list);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<RecycleItemEntity> recycles = recycleItemRepository.downloadCsvByIds(list);
        return CsvExporter.export(recycles, RecycleItemEntity.class);
    }
}
