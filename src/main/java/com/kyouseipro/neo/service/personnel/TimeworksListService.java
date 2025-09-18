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

    // /**
    //  * 指定された従業員IDの勤怠情報を取得します。
    //  * 論理削除されている場合は null を返します。
    //  *
    //  * @param id 従業員ID
    //  * @return TimeworksListEntity または null
    //  */
    // public TimeworksListEntity getEmployeeById(int id) {
    //     return timeworksListRepository.findById(id);
    // }

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
    public TimeworksListEntity getTodaysEntityByEmployeeId(int id) {
        return timeworksListRepository.findByTodaysEntityByEmployeeId(id);
    }

    /**
     * 指定された従業員IDの指定した期間の勤怠情報を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return TimeworksListEntity または null
     */
    public List<TimeworksListEntity> getBetweenEntityByEmployeeId(int id, LocalDate start, LocalDate end) {
        return timeworksListRepository.findByEmployeeIdFromBetweenDate(id, start, end);
    }

    /**
     * 指定された期間の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return TimeworksSummaryListEntity または null
     */
    public List<TimeworksSummaryEntity> getBetweenSummaryEntity(LocalDate start, LocalDate end) {
        return timeworksListRepository.findByEntityFromBetweenDate(start, end);
    }

    /**
     * 指定された営業所IDの指定した期間の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return TimeworksSummaryEntity または null
     */
    public List<TimeworksSummaryEntity> getBetweenSummaryEntityByOfficeId(int id, LocalDate start, LocalDate end) {
        return timeworksListRepository.findByOfficeIdFromBetweenDate(id, start, end);
    }


    /**
     * 指定された営業所IDの指定した年の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return PaidHolidayListEntity または null
     */
    public List<PaidHolidayListEntity> getPaidHolidayEntityFromYear(int id, String year) {
        return timeworksListRepository.findPaidHolidayByOfficeIdFromYear(id, year);
    }

    /**
     * 指定された従業員IDの指定した年の有給リストを取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return PaidHolidayEntity または null
     */
    public List<PaidHolidayEntity> getPaidHolidayEntityByEmployeeId(int id, String year) {
        return timeworksListRepository.findPaidHolidayByEmployeeIdFromYear(id, year);
    }

    /**
     * 従業員の有給申請情報を保存します。
     *
     * @param entity 従業員の有給申請データ
     * @return
     */
    public Integer savePaidHolidayEntityByEmployeeId(PaidHolidayEntity entity, String editor) {
        return timeworksListRepository.insertPaidHolidayByEmployeeId(entity, editor);
    }

    /**
     * 従業員の有給申請情報を削除します。
     *
     * @param id 従業員の有給申請ID
     * @return
     */
    public Integer deletePaidHolidayEntityById(int id, String editor) {
        return timeworksListRepository.deletePaidHolidayEntityById(id, editor);
    }

    /**
     * 従業員の今日の勤怠情報を保存します。
     *
     * @param entity 従業員の勤怠データ
     * @return
     */
    public Integer saveTodaysTimeworks(TimeworksListEntity entity, String editor) {
        if (entity.getTimeworks_id() > 0) {
            return timeworksListRepository.updateTimeworks(entity, editor);
        } else {
            return timeworksListRepository.insertTimeworks(entity, editor);
        }
    }

    /**
     * 修正した従業員の勤怠情報を保存します。
     *
     * @param list 修正した従業員の勤怠データリスト
     * @return
     */
    public Integer updateTimeworksList(List<TimeworksListEntity> list, String editor) {
        return timeworksListRepository.updateTimeworksList(list, editor);
    }

    /**
     * IDからCsv用文字列を取得
     * @param ids
     * @return
     */
    public String downloadCsvByIds(List<SimpleData> list, String start, String end, String userName) {
        List<TimeworksListEntity> items = timeworksListRepository.downloadCsvByIds(list, start, end, userName);
        return CsvExporter.export(items, TimeworksListEntity.class);
    }
}
