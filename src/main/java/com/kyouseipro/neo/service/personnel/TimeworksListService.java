package com.kyouseipro.neo.service.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.controller.document.CsvExporter;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksSummaryEntity;
import com.kyouseipro.neo.repository.personnel.TimeworksListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworksListService {
    private final TimeworksListRepository timeworksListRepository;

    /**
     * 今日の前従業員勤怠データを取得する
     * 
     * @return TimeworksListEntityのリスト
     */
    public List<TimeworksListEntity> getTodaysList() {
        LocalDate date = LocalDate.now();
        return timeworksListRepository.findByDate(date);
    }

    /**
     * 指定された従業員IDの今日の勤怠情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return TimeworksListEntity または null
     */
    public TimeworksListEntity getTodaysByEmployeeId(int id) {
        return timeworksListRepository.findByTodaysByEmployeeId(id);
    }

    /**
     * 指定された従業員IDの指定した期間の勤怠情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return TimeworksListEntity または null
     */
    public List<TimeworksListEntity> getBetweenByEmployeeId(int id, LocalDate start, LocalDate end) {
        return timeworksListRepository.findByBetweenByEmployeeId(id, start, end);
    }

    /**
     * 指定された従業員IDの指定した期間の確定済みも含めた勤怠情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return TimeworksListEntity または null
     */
    public List<TimeworksListEntity> getBetweenAllByEmployeeId(int id, LocalDate start, LocalDate end) {
        return timeworksListRepository.findAllByBetweenByEmployeeId(id, start, end);
    }

    /**
     * 指定された期間の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return TimeworksSummaryListEntity または null
     */
    public List<TimeworksSummaryEntity> getBetweenSummary(LocalDate start, LocalDate end) {
        return timeworksListRepository.findByBetweenSummary(start, end);
    }

    /**
     * 指定された営業所IDの指定した期間の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return TimeworksSummaryEntity または null
     */
    public List<TimeworksSummaryEntity> getBetweenSummaryByOfficeId(int id, LocalDate start, LocalDate end) {
        return timeworksListRepository.findByBetweenSummaryByOfficeId(id, start, end);
    }

    /**
     * 従業員の今日の勤怠情報を保存します。
     *
     * @param entity 従業員の勤怠データ
     * @return
     */
    public Integer save(TimeworksListEntity entity, String editor) {
        if (entity.getTimeworks_id() > 0) {
            return timeworksListRepository.update(entity, editor);
        } else {
            return timeworksListRepository.insert(entity, editor);
        }
    }

    /**
     * 従業員の勤怠情報確定を取り消します。
     *
     * @param id 従業員のID
     * @return
     */
    public Integer reverseConfirm(int id, String editor) {
        return timeworksListRepository.reverseConfirm(id, editor);
    }

    /**
     * 修正した従業員の勤怠情報を保存します。
     *
     * @param list 修正した従業員の勤怠データリスト
     * @return
     */
    public Integer updateList(List<TimeworksListEntity> list, String editor) {
        return timeworksListRepository.updateList(list, editor);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIdsFromBetween(List<SimpleData> list, String start, String end, String userName) {
        List<TimeworksListEntity> items = timeworksListRepository.downloadCsvByIdsFromBetween(list, start, end, userName);
        return CsvExporter.export(items, TimeworksListEntity.class);
    }
}
