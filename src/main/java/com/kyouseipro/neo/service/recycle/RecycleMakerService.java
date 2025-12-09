package com.kyouseipro.neo.service.recycle;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.query.sql.recycle.RecycleMakerSqlBuilder;
import com.kyouseipro.neo.repository.recycle.RecycleMakerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleMakerService {
    private final RecycleMakerRepository recycleMakerRepository;

    /**
     * 指定されたIDのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id リサイクルID
     * @return RecycleEntity または null
     */
    public RecycleMakerEntity getRecycleMakerById(String sql, int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return recycleMakerRepository.findById(sql, id);
    }

    /**
     * リサイクル情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    public Integer saveRecycleMaker(RecycleMakerEntity entity) {
        if (entity.getRecycle_maker_id() > 0) {
            return recycleMakerRepository.updateRecycleMaker(RecycleMakerSqlBuilder.buildUpdateRecycleMakerSql(), entity);
        } else {
            return recycleMakerRepository.insertRecycleMaker(RecycleMakerSqlBuilder.buildInsertRecycleMakerSql(), entity);
        }
    }

    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteRecycleMakerByIds(List<SimpleData> list) {
        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(list);
        String sql = RecycleMakerSqlBuilder.buildDeleteRecycleMakerForIdsSql(recycleMakerIds.size());
        return recycleMakerRepository.deleteRecycleMakerByIds(sql, recycleMakerIds);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvRecycleMakerByIds(List<SimpleData> list) {
        List<Integer> recycleMakerIds = Utilities.createSequenceByIds(list);
        String sql = RecycleMakerSqlBuilder.buildDownloadCsvRecycleMakerForIdsSql(recycleMakerIds.size());
        List<RecycleMakerEntity> recycles = recycleMakerRepository.downloadCsvRecycleMakerByIds(sql, recycleMakerIds);
        return CsvExporter.export(recycles, RecycleMakerEntity.class);
    }
}
