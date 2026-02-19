package com.kyouseipro.neo.personnel.timeworks.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Enums.HistoryTables;
import com.kyouseipro.neo.interfaces.HistoryTarget;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.repository.EmployeeRepository;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksEntity;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksRequestDto;
import com.kyouseipro.neo.personnel.timeworks.repository.TimeworksRepository;
import com.kyouseipro.neo.personnel.timeworks.repository.TimeworksSqlBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworksService {

    private final TimeworksRepository timeworksRepository;
    private final EmployeeRepository employeeRepository;

    public List<TimeworksEntity> getTodaysList() {
        LocalDate date = LocalDate.now();
        return timeworksRepository.findAllByBaseDate(date);
    }

    // /**
    //  * 指定したIDの今日の勤怠情報を取得する
    //  * @return
    //  */
    // public TimeworksEntity getTodaysEntity(Integer employeeId) {

    //     LocalDate today = calcWorkBaseDate(LocalDateTime.now());

    //     TimeworksEntity opt = timeworksRepository.findToday(employeeId, today);

    //     if (opt.isPresent()) {
    //         return opt.get();
    //     }

    //     // ===== 勤怠なし → 従業員情報から空エンティティ作成 =====
    //     EmployeeEntity emp = employeeRepository.findById(employeeId)
    //             .orElseThrow(() ->
    //                     new IllegalArgumentException("従業員が存在しません id=" + employeeId)
    //             );

    //     TimeworksEntity e = new TimeworksEntity();
    //     e.setEmployeeId(emp.getEmployeeId());
    //     e.setFullName(emp.getFullName());
    //     e.setOfficeName(emp.getOfficeName());

    //     e.setWorkBaseDate(today);
    //     e.setState(Enums.timeworksState.NOT_STARTED);
    //     e.setWorkType(Enums.timeworksType.NORMAL);

    //     e.setStartDt(null);
    //     e.setEndDt(null);
    //     e.setBreakMinutes(60);

    //     return e;
    // }

    public TimeworksEntity getTodaysEntity(Integer id) {
        EmployeeEntity emp = employeeRepository.findById(id);
        if (emp == null) {
            throw new IllegalArgumentException("従業員が存在しません");
        }

        LocalDate today = calcWorkBaseDate(LocalDateTime.now());

        TimeworksEntity entity = timeworksRepository.findToday(emp.getEmployeeId(), today);
        if (entity != null) {
            return entity;
        }
        return createDefaultEntity(emp, today);
    }

    private TimeworksEntity createDefaultEntity(EmployeeEntity emp, LocalDate today) {

        TimeworksEntity e = new TimeworksEntity();
        e.setEmployeeId(emp.getEmployeeId());
        e.setFullName(emp.getFullName());
        e.setOfficeName(emp.getOfficeName());
        e.setWorkBaseDate(today);
        e.setState(Enums.timeworksState.NOT_STARTED);
        e.setWorkType(Enums.timeworksType.NORMAL);
        e.setStartDt(null);
        e.setEndDt(null);
        e.setBreakMinutes(60);

        return e;
    }

    /**
     * 今日の勤怠データを取得
     * @param dto
     * @param category
     * @return
     */
    @HistoryTarget(
        table = HistoryTables.TIMEWORKS,
        action = "保存"
    )
    public int registToday(TimeworksRequestDto dto, Enums.timeworksCategory category) {

        LocalDateTime now = LocalDateTime.now();
        LocalDate workBaseDate = calcWorkBaseDate(now);

        TimeworksEntity opt = timeworksRepository.findToday(
            dto.getEmployeeId(),
            workBaseDate
        );

        // ===== insert =====
        if (opt == null) {

            TimeworksEntity e = new TimeworksEntity();
            e.setEmployeeId(dto.getEmployeeId());
            e.setWorkBaseDate(workBaseDate);

            // ★ 共通初期値（超重要）
            e.setBreakMinutes(60);
            e.setWorkType(Enums.timeworksType.NORMAL);
            e.setState(Enums.timeworksState.WORKING);

            // 緯度経度は「未使用＝0」で統一（DBが NOT NULL 前提）
            e.setStartLatitude(BigDecimal.ZERO);
            e.setStartLongitude(BigDecimal.ZERO);
            e.setEndLatitude(BigDecimal.ZERO);
            e.setEndLongitude(BigDecimal.ZERO);

            if (category == Enums.timeworksCategory.START) {

                e.setStartDt(now);
                e.setStartLatitude(dto.getStartLatitude());
                e.setStartLongitude(dto.getStartLongitude());

            } else { // END

                // 出勤押し忘れ
                e.setStartDt(getDefaultStartTime(workBaseDate));
                e.setEndDt(now);

                e.setEndLatitude(dto.getEndLatitude());
                e.setEndLongitude(dto.getEndLongitude());

                e.setState(Enums.timeworksState.FINISHED);
                e.setWorkType(Enums.timeworksType.AUTO_START);
            }

            return timeworksRepository.insert(e);
        }

        // ===== update =====
        TimeworksEntity e = opt;

        if (category == Enums.timeworksCategory.START && e.getStartDt() == null) {
            e.setStartDt(now);
            e.setStartLatitude(dto.getStartLatitude());
            e.setStartLongitude(dto.getStartLongitude());
            e.setState(Enums.timeworksState.WORKING);

            if (e.getWorkType() == null) {
                e.setWorkType(Enums.timeworksType.NORMAL);
            }
        }

        if (category == Enums.timeworksCategory.END) {
            if (e.getStartDt() == null) {
                e.setStartDt(getDefaultStartTime(workBaseDate));
                e.setWorkType(Enums.timeworksType.AUTO_START);
            }

            e.setEndDt(now);
            e.setEndLatitude(dto.getEndLatitude());
            e.setEndLongitude(dto.getEndLongitude());
            e.setState(Enums.timeworksState.FINISHED);
        }

        int count = timeworksRepository.update(e);
        if (count != 1) {
            throw new IllegalStateException("勤怠更新失敗 count=" + count);
        }

        return count;
    }

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);

    private LocalDateTime getDefaultStartTime(LocalDate workBaseDate) {
        return LocalDateTime.of(workBaseDate, DEFAULT_START_TIME);
    }
    
    /**
     * 夜勤基準日ロジック
     * @param startDt
     * @return
     */
    private LocalDate calcWorkBaseDate(LocalDateTime dt) {
        if (dt.getHour() < 5) {
            return dt.toLocalDate().minusDays(1);
        }
        return dt.toLocalDate();
    }
}