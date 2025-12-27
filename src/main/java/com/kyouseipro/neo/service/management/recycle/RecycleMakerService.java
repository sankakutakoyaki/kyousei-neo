package com.kyouseipro.neo.service.management.recycle;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.repository.management.recycle.RecycleMakerRepository;

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
    public RecycleMakerEntity getRecycleMakerById(int id) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return recycleMakerRepository.findById(id);
    }

    /**
     * 指定されたCodeのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param code
     * @return RecycleEntity または null
     */
    public RecycleMakerEntity getRecycleMakerByCode(int code) {
        // String sql = OrderSqlBuilder.buildFindByIdSql();
        return recycleMakerRepository.findByCode(code);
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
            return recycleMakerRepository.updateRecycleMaker(entity);
        } else {
            return recycleMakerRepository.insertRecycleMaker(entity);
        }
    }

    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteRecycleMakerByIds(List<SimpleData> list) {
        return recycleMakerRepository.deleteRecycleMakerByIds(list);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvRecycleMakerByIds(List<SimpleData> list) {
        List<RecycleMakerEntity> recycles = recycleMakerRepository.downloadCsvRecycleMakerByIds(list);
        return CsvExporter.export(recycles, RecycleMakerEntity.class);
    }
}
