package com.kyouseipro.neo.recycle.regist.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.recycle.regist.entity.RecycleDateEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;
import com.kyouseipro.neo.recycle.regist.repository.RecycleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleService {
    private final RecycleRepository recycleRepository;

    /**
     * Numberによる取得。
     * @param number
     * @return Numberから取得したEntityを返す。
     */
    public RecycleEntity getByNumber(String number) {
        return recycleRepository.findByNumber(number);
    }
    
    /**
     * 指定した期間のEntityを取得。
     * @param start
     * @param end
     * @param col
     * @return
     */
    public List<RecycleEntity> getBetween(LocalDate start, LocalDate end, String col) {
        return recycleRepository.findByBetween(start, end, col);
    }

    /**
     * Entityを登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.RECYCLES,
        action = "保存"
    )
    public RecycleEntity save(RecycleEntity entity, String editor) {
        int id = recycleRepository.save(entity, editor);
        if (id > 0) {
            return recycleRepository.findById(id);
        } else {
            return null;
        }
    }

    /**
     * リサイクル情報を更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.RECYCLEITEMS,
        action = "更新"
    )
    public RecycleEntity update(RecycleEntity entity, String type, String editor) {
        int id = recycleRepository.update(entity, type, editor);
        if (id > 0) {
            return recycleRepository.findById(id);
        } else {
            return null;
        }
    }

    @HistoryTarget(
        table = HistoryTables.RECYCLEITEMS,
        action = "一括更新"
    )
    public int bulkUpdate(RecycleEntityRequest entity) {
        return recycleRepository.bulkUpdate(entity);
    }

    /**
     * 日付情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    @HistoryTarget(
        table = HistoryTables.RECYCLEITEMS,
        action = "日付更新"
    )
    public int updateForDate(RecycleDateEntity entity, String editor, String type) {
        return recycleRepository.updateForDate(entity, editor, type);
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
        return recycleRepository.deleteByIds(list, userName);
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<RecycleEntity> recycles = recycleRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(recycles, RecycleEntity.class);
    }
}
