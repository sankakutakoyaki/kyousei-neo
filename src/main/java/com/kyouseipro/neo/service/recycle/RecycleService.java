package com.kyouseipro.neo.service.recycle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleDateEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.repository.recycle.RecycleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleService {
    private final RecycleRepository recycleRepository;

    /**
     * 指定されたIDのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id リサイクルID
     * @return RecycleEntity または null
     */
    public RecycleEntity getById(int id) {
        return recycleRepository.findById(id);
    }

    /**
     * 指定されたNumberのリサイクル情報存在するか確認
     * 論理削除されている場合は null を返します。
     *
     * @param number リサイクルID
     * @return RecycleEntity または null
     */
    public RecycleEntity existsByNumber(String str) {
        return recycleRepository.existsByNumber(str);
    }

    /**
     * 
     * @param sql
     * @param number
     * @return
     */
    public RecycleEntity getByNumber(String number) {
        return recycleRepository.findByNumber(number);
    }
    
    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    public List<RecycleEntity> getBetween(LocalDate start, LocalDate end, String col) {
        return recycleRepository.findByBetween(start, end, col);
    }

    /**
     * リサイクル情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer save(List<RecycleEntity> itemList, String editor) {
        return recycleRepository.save(itemList, editor);
    }

    /**
     * リサイクル情報を更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer update(RecycleEntity entity, String editor) {
        return recycleRepository.update(entity, editor);
    }

    /**
     * 日付情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer updateForDate(List<RecycleDateEntity> itemList, String editor, String type) {
        return recycleRepository.updateForDate(itemList, editor, type);
    }

    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteByIds(List<SimpleData> list, String userName) {
        return recycleRepository.deleteByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list, String userName) {
        List<RecycleEntity> recycles = recycleRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(recycles, RecycleEntity.class);
    }
}
