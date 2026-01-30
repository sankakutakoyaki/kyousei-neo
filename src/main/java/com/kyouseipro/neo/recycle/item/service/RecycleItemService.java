package com.kyouseipro.neo.recycle.item.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.recycle.item.entity.RecycleItemEntity;
import com.kyouseipro.neo.recycle.item.repository.RecycleItemRepository;

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
    @HistoryTarget(
        table = HistoryTables.RECYCLEITEMS,
        action = "保存"
    )
    public int save(RecycleItemEntity entity, String userName) {
        if (entity.getRecycleItemId() > 0) {
            return recycleItemRepository.update(entity, userName);
        } else {
            return recycleItemRepository.insert(entity, userName);
        }
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param list
     * @param editor
     * @return 成功件数を返す。
     */
    @HistoryTarget(
        table = HistoryTables.RECYCLEITEMS,
        action = "削除"
    )
    public int deleteByIds(IdListRequest list, String userName) {
        return recycleItemRepository.deleteByIds(list, userName);
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<RecycleItemEntity> recycles = recycleItemRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(recycles, RecycleItemEntity.class);
    }
}
