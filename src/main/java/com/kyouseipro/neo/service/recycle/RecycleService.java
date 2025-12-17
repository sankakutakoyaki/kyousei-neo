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
    public RecycleEntity getRecycleById(int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return recycleRepository.findById(id);
    }

    /**
     * 指定されたNumberのリサイクル情報存在するか確認
     * 論理削除されている場合は null を返します。
     *
     * @param number リサイクルID
     * @return RecycleEntity または null
     */
    public RecycleEntity existsRecycleByNumber(String str) {
        // return existsRepository.existsRecycleByNumber(str);
        return recycleRepository.existsRecycleByNumber(str);
    }
    // public RecycleEntity existsRecycleByNumber(String number) {
    //     // String sql = OrderSqlBuilder.buildFindByIdSql();
    //     return recycleRepository.existsByNumber(number);
    // }

    /**
     * 
     * @param sql
     * @param number
     * @return
     */
    public RecycleEntity getRecycleByNumber(String number) {
        return recycleRepository.findByNumber(number);
    }
    
    /**
     * 
     * @param start
     * @param end
     * @param col
     * @return
     */
    public List<RecycleEntity> getBetweenRecycleEntity(LocalDate start, LocalDate end, String col) {
        return recycleRepository.findByEntityFromBetweenDate(start, end, col);
    }

    /**
     * リサイクル情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveRecycle(List<RecycleEntity> itemList, String editor) {
        // String sql = "";
        // int index = 1;

        // for (RecycleEntity entity : itemList) {
        //     if (entity.getState() == Enums.state.DELETE.getCode()) {
        //         sql += RecycleSqlBuilder.buildDeleteRecycleSql(index++);
        //     } else {
        //         if (entity.getRecycle_id() > 0) {
        //             sql += RecycleSqlBuilder.buildUpdateRecycleSql(index++);
        //         } else {
        //             sql += RecycleSqlBuilder.buildInsertRecycleSql(index++);
        //         }
        //     }
        // }
        return recycleRepository.saveRecycleList(itemList, editor);
    }

    /**
     * リサイクル情報を更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer updateRecycle(RecycleEntity entity, String editor) {
        return recycleRepository.updateRecycle(entity, editor);
    }

    /**
     * 日付情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer updateRecycleDate(List<RecycleDateEntity> itemList, String editor, String type) {
        return recycleRepository.updateRecycleDateList(itemList, editor, type);
    }

    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteRecycleByIds(List<SimpleData> list, String userName) {
        // List<Integer> recycleIds = Utilities.createSequenceByIds(list);
        // String sql = RecycleSqlBuilder.buildDeleteRecycleForIdsSql(recycleIds.size());
        return recycleRepository.deleteRecycleByIds(list, userName);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvRecycleByIds(List<SimpleData> list, String userName) {
        // List<Integer> recycleIds = Utilities.createSequenceByIds(list);
        // String sql = RecycleSqlBuilder.buildDownloadCsvRecycleForIdsSql(recycleIds.size());
        List<RecycleEntity> recycles = recycleRepository.downloadCsvRecycleByIds(list, userName);
        return CsvExporter.export(recycles, RecycleEntity.class);
    }
}
