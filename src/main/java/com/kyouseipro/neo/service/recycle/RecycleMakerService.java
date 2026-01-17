package com.kyouseipro.neo.service.recycle;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.controller.dto.CsvExporter;
import com.kyouseipro.neo.entity.dto.IdListRequest;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
import com.kyouseipro.neo.repository.recycle.RecycleMakerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleMakerService {
    // private final HistoryService historyService;
    private final RecycleMakerRepository recycleMakerRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public Optional<RecycleMakerEntity> getById(int id) {
        return recycleMakerRepository.findById(id);
    }

    /**
     * Codeによる取得。
     * @param code
     * @return Codeから取得したEntityを返す。
     */
    public Optional<RecycleMakerEntity> getByCode(int code) {
        return recycleMakerRepository.findByCode(code);
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<RecycleMakerEntity> getList() {
        return recycleMakerRepository.findAll();
    }

    /**
     * Entityを登録・更新します。
     * IDが０の時は登録・０以上の時は更新します。
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
    @HistoryTarget(
        table = HistoryTables.RECYCLEMAKERS,
        action = "保存"
    )
    public int save(RecycleMakerEntity entity, String userName) {
        int id = entity.getRecycle_maker_id();

        if (id > 0) {
            id = recycleMakerRepository.update(entity);
        } else {
            id = recycleMakerRepository.insert(entity);
        }

        return id;
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
        return recycleMakerRepository.deleteByIds(list, userName);
    }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param list
     * @param editor
     * @return listで選択したEntityリストを返す。
     */
    public String downloadCsvByIds(IdListRequest list, String userName) {
        List<RecycleMakerEntity> recycles = recycleMakerRepository.downloadCsvByIds(list, userName);
        return CsvExporter.export(recycles, RecycleMakerEntity.class);
    }
}
