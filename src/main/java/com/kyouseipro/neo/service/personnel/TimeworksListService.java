package com.kyouseipro.neo.service.personnel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

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
     * 指定された営業所IDの指定した期間の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return TimeworksSummaryListEntity または null
     */
    public List<TimeworksSummaryEntity> getBetweenSummaryEntityByEmployeeId(int id, LocalDate start, LocalDate end) {
        return timeworksListRepository.findByOfficeIdFromBetweenDate(id, start, end);
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
}
