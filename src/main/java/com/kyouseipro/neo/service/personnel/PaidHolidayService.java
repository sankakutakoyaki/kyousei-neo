package com.kyouseipro.neo.service.personnel;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.entity.personnel.PaidHolidayEntity;
import com.kyouseipro.neo.entity.personnel.PaidHolidayListEntity;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
import com.kyouseipro.neo.repository.personnel.PaidHolidayRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaidHolidayService {
    private final PaidHolidayRepository paidHolidayRepository;

    /**
     * 指定された営業所IDの指定した年の勤怠情報概要を取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 営業所ID
     * @return PaidHolidayListEntity または null
     */
    public List<PaidHolidayListEntity> getByOfficeIdFromYear(int id, String year) {
        return paidHolidayRepository.findByOfficeIdFromYear(id, year);
    }

    /**
     * 指定された従業員IDの指定した年の有給リストを取得します。
     * 論理削除されている場合は null を返します。
     *
     * @param id 従業員ID
     * @return PaidHolidayEntity または null
     */
    public List<PaidHolidayEntity> getByEmployeeIdFromYear(int id, String year) {
        return paidHolidayRepository.findByEmployeeIdFromYear(id, year);
    }

    /**
     * 従業員の有給申請情報を保存します。
     *
     * @param entity 従業員の有給申請データ
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.PAIDHOLIDAY,
        action = "登録"
    )
    public int insert(PaidHolidayEntity entity, String editor) {
        return paidHolidayRepository.insert(entity, editor);
    }

    /**
     * 従業員の有給申請情報を削除します。
     *
     * @param id 従業員の有給申請ID
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.PAIDHOLIDAY,
        action = "削除"
    )
    public int deleteById(int id, String editor) {
        return paidHolidayRepository.delete(id, editor);
    }
}
