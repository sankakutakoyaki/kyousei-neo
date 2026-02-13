package com.kyouseipro.neo.recycle.maker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.dto.CsvExporter;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.recycle.maker.entity.RecycleMakerEntity;
import com.kyouseipro.neo.recycle.maker.repository.RecycleMakerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecycleMakerService {
    private final RecycleMakerRepository recycleMakerRepository;

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public RecycleMakerEntity getById(int id) {
        return recycleMakerRepository.findById(id);
    }

    /**
     * Codeによる取得。
     * @param code
     * @return Codeから取得したEntityを返す。
     */
    public RecycleMakerEntity getByCode(int code) {
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
    @HistoryTarget(
        table = HistoryTables.RECYCLEMAKERS,
        action = "保存"
    )
    public int save(RecycleMakerEntity entity, String userName) {
        int id = entity.getRecycleMakerId();

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
