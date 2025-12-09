package com.kyouseipro.neo.service.recycle;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleItemEntity;
import com.kyouseipro.neo.query.sql.recycle.RecycleItemSqlBuilder;
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
    public RecycleItemEntity getRecycleItemById(String sql, int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return recycleItemRepository.findById(sql, id);
    }

    /**
     * リサイクル情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveRecycleItem(RecycleItemEntity entity) {
        if (entity.getRecycle_item_id() > 0) {
            return recycleItemRepository.updateRecycleItem(RecycleItemSqlBuilder.buildUpdateRecycleItemSql(), entity);
        } else {
            return recycleItemRepository.insertRecycleItem(RecycleItemSqlBuilder.buildInsertRecycleItemSql(), entity);
        }
    }

    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteRecycleItemByIds(List<SimpleData> list) {
        List<Integer> recycleItemIds = Utilities.createSequenceByIds(list);
        String sql = RecycleItemSqlBuilder.buildDeleteRecycleItemForIdsSql(recycleItemIds.size());
        return recycleItemRepository.deleteRecycleItemByIds(sql, recycleItemIds);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvRecycleItemByIds(List<SimpleData> list) {
        List<Integer> recycleItemIds = Utilities.createSequenceByIds(list);
        String sql = RecycleItemSqlBuilder.buildDownloadCsvRecycleItemForIdsSql(recycleItemIds.size());
        List<RecycleItemEntity> recycles = recycleItemRepository.downloadCsvRecycleItemByIds(sql, recycleItemIds);
        return CsvExporter.export(recycles, RecycleItemEntity.class);
    }
}
