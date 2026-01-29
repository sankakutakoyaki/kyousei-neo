package com.kyouseipro.neo.service.personnel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.dto.TimeworksRequestDto;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksEntity;
import com.kyouseipro.neo.repository.personnel.EmployeeRepository;
import com.kyouseipro.neo.repository.personnel.TimeworksRepository;

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

    /**
     * 指定したIDの今日の勤怠情報を取得する
     * @return
     */
    public Optional<TimeworksEntity> getTodaysEntity(Integer employeeId) {

        LocalDate today = calcWorkBaseDate(LocalDateTime.now());

        Optional<TimeworksEntity> opt =
            timeworksRepository.findToday(employeeId, today);

        if (opt.isPresent()) {
            return opt;
        }

        // ===== 勤怠なし → 従業員情報から空エンティティ作成 =====
        EmployeeEntity emp = employeeRepository.findById(employeeId)
            .orElseThrow(() ->
                new IllegalArgumentException("従業員が存在しません id=" + employeeId)
            );

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

        return Optional.of(e);
    }

    /**
     * 今日の勤怠データを取得
     * @param dto
     * @param category
     * @return
     */
    public int registToday(TimeworksRequestDto dto, Enums.timeworksCategory category) {

        LocalDateTime now = LocalDateTime.now();
        LocalDate workBaseDate = calcWorkBaseDate(now);

        Optional<TimeworksEntity> opt = timeworksRepository.findToday(
            dto.getEmployeeId(),
            workBaseDate
        );

        // ===== insert =====
        if (opt.isEmpty()) {

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
        TimeworksEntity e = opt.get();

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