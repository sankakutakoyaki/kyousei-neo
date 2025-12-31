package com.kyouseipro.neo.service.recycle;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.repository.recycle.RecycleMakerRepository;
import com.kyouseipro.neo.service.document.HistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleMakerService {
    private final HistoryService historyService;
    private final RecycleMakerRepository recycleMakerRepository;

    /**
     * 指定されたIDのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id リサイクルID
     * @return RecycleEntity または null
     */
    public RecycleMakerEntity getById(int id) {
        return recycleMakerRepository.findById(id);
    }

    /**
     * 指定されたCodeのリサイクル情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param code
     * @return RecycleEntity または null
     */
    public RecycleMakerEntity getByCode(int code) {
        return recycleMakerRepository.findByCode(code);
    }

    /**
     * すべてのrecycle_makerを取得
     * @return
     */
    public List<RecycleMakerEntity> getList() {
        return recycleMakerRepository.findAll();
    }

    /**
     * リサイクル情報を登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
     * 
     * @param entity
     * @param editor
     * @return 成功した場合はIDまたは更新件数を返す。失敗した場合は０を返す。
    */
    // public Integer save(RecycleMakerEntity entity) {
    //     Integer id = entity.getRecycle_maker_id();
    //     if (id != null && id > 0) {
    //         return recycleMakerRepository.update(entity);
    //     }
    //     return recycleMakerRepository.insert(entity);
    // }
    // @Transactional
    public Integer save(RecycleMakerEntity entity, String userName) {
        Integer id = entity.getRecycle_maker_id();

        if (id != null && id > 0) {
            id = recycleMakerRepository.update(entity);
        } else {
            id = recycleMakerRepository.insert(entity);
        }

        return id;
    }


    /**
     * IDからRecycleを削除
     * @param ids
     * @return
     */
    public Integer deleteByIds(List<SimpleData> list) {
        return recycleMakerRepository.deleteByIds(list);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list) {
        List<RecycleMakerEntity> recycles = recycleMakerRepository.downloadCsvByIds(list);
        return CsvExporter.export(recycles, RecycleMakerEntity.class);
    }
}
