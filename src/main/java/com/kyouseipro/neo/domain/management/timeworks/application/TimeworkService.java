package com.kyouseipro.neo.domain.management.timeworks.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.domain.management.timeworks.model.OriginalTimework;
import com.kyouseipro.neo.domain.management.timeworks.model.SelfStampRequest;
import com.kyouseipro.neo.domain.management.timeworks.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.timeworks.model.TimeworkPeriod;
import com.kyouseipro.neo.domain.management.timeworks.model.TimeworkStatus;
import com.kyouseipro.neo.domain.management.timeworks.model.TimeworkUpdateRequest;
import com.kyouseipro.neo.domain.management.timeworks.repository.TimeworkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworkService {
    private static final Logger log = LoggerFactory.getLogger(TimeworkService.class);

    private final TimeworkRepository repository;
    private final TimeworkLocationService locationService;

    public TimeworkStatus findEmployee(String identifier) {
        String normalized = identifier == null ? "" : identifier.trim();
        if (normalized.isEmpty()) throw new BusinessException("社員IDまたはコードを入力してください。");
        TimeworkStatus status = repository.findTodayByIdentifier(normalized, LocalDate.now());
        if (status == null) throw new BusinessException("該当する社員が見つかりません。");
        return status;
    }

    public TimeworkStatus findSelf(Authentication authentication) {
        TimeworkStatus status = repository.findTodayByAccount(account(authentication), LocalDate.now());
        if (status == null) throw new BusinessException("ログインユーザーに対応する社員が登録されていません。");
        return status;
    }

    // 打刻画面では、全営業所の当日出勤予定者を取得する
    public List<TimeworkListItem> findTodayList() {
        return repository.findTodayShiftList(LocalDate.now());
    }

    public List<TimeworkListItem> findManagementList(
        String targetMonth, String closingType, Long officeId, Long employeeId
    ) {
        TimeworkPeriod period = period(targetMonth, closingType);
        return repository.findManagementList(period.from(), period.to(), officeId, employeeId);
    }

    public List<ComboDto> findEmployeeCombo(Long officeId) {
        return repository.findEmployeeCombo(officeId);
    }

    public TimeworkPeriod period(String targetMonth, String closingType) {
        final YearMonth month;
        try {
            month = YearMonth.parse(targetMonth);
        } catch (RuntimeException exception) {
            throw new BusinessException("対象月を選択してください。");
        }
        if ("FIFTEENTH".equals(closingType)) {
            return new TimeworkPeriod(
                month.minusMonths(1).atDay(16), month.atDay(15),
                month + " 15日締め"
            );
        }
        if ("MONTH_END".equals(closingType)) {
            return new TimeworkPeriod(month.atDay(1), month.atEndOfMonth(), month + " 月末締め");
        }
        throw new BusinessException("締め日を選択してください。");
    }

    @Transactional
    public void updateTimes(Authentication authentication, TimeworkUpdateRequest request) {
        if (request.timeworkId() <= 0) throw new BusinessException("勤怠データの指定が不正です。");
        OriginalTimework original = repository.findOriginal(request.timeworkId());
        if (original == null) throw new BusinessException("勤怠データが見つかりません。");
        LocalDateTime effectiveStart = request.editStartTime() != null
            ? request.editStartTime() : original.startTime();
        LocalDateTime effectiveEnd = request.editEndTime() != null
            ? request.editEndTime() : original.endTime();
        if (effectiveStart == null) throw new BusinessException("有効な出勤時刻がありません。");
        if (effectiveEnd != null && effectiveEnd.isBefore(effectiveStart)) {
            throw new BusinessException("退勤時刻は出勤時刻以降を入力してください。");
        }
        repository.saveEdit(
            request.timeworkId(), request.editStartTime(), request.editEndTime(), account(authentication)
        );
    }

    @Transactional
    public TimeworkStatus stamp(Authentication authentication, long employeeId, String stampType) {
        if (employeeId <= 0) throw new BusinessException("社員を選択してください。");
        String type = stampType == null ? "" : stampType.trim().toUpperCase(Locale.ROOT);
        if (!type.equals("START") && !type.equals("END")) throw new BusinessException("打刻種別が不正です。");

        LocalDateTime now = LocalDateTime.now();
        String account = account(authentication);
        TimeworkStatus status = repository.findTodayByEmployeeId(employeeId, now.toLocalDate());
        if (status == null) throw new BusinessException("該当する社員が見つかりません。");
        if (type.equals("START")) {
            if (!status.canStart()) throw new BusinessException("本日はすでに出勤打刻されています。");
            repository.insertStart(status.employeeId(), now.toLocalDate(), now, account);
        } else {
            if (!status.canEnd()) throw new BusinessException("退勤できる勤務状態ではありません。");
            repository.updateEnd(status.timeworkId(), now, account);
        }
        return repository.findTodayByEmployeeId(employeeId, now.toLocalDate());
    }

    /**
     * 本人打刻を登録し、確定後に位置情報を別トランザクションで保存する。
     */
    @Transactional
    public TimeworkStatus stampSelf(Authentication authentication, SelfStampRequest request) {
        TimeworkStatus self = findSelf(authentication);
        TimeworkStatus result = stamp(authentication, self.employeeId(), request.stampType());

        // GPS情報が有効な場合だけ保存を予約する
        if (hasValidLocation(request) && result.timeworkId() != null) {
            long timeworkId = result.timeworkId();
            String type = request.stampType().trim().toUpperCase(Locale.ROOT);
            String editor = account(authentication);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        locationService.save(
                            timeworkId, type,
                            request.latitude(), request.longitude(), request.accuracy(),
                            request.recordedAt(), editor
                        );
                    } catch (Exception error) {
                        // GPS保存に失敗しても確定済みの勤怠打刻は維持する
                        log.warn("打刻位置情報の保存に失敗しました。timeworkId={}, type={}",
                            timeworkId, type, error);
                    }
                }
            });
        }

        return result;
    }

    /** GPS情報が正常な場合のみ保存対象とする。 */
    private boolean hasValidLocation(SelfStampRequest request) {
        Double latitude = request.latitude();
        Double longitude = request.longitude();
        Double accuracy = request.accuracy();

        return latitude != null && Double.isFinite(latitude)
            && latitude >= -90 && latitude <= 90
            && longitude != null && Double.isFinite(longitude)
            && longitude >= -180 && longitude <= 180
            && accuracy != null && Double.isFinite(accuracy) && accuracy >= 0
            && request.recordedAt() != null;
    }

    private String account(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) throw new BusinessException("ログイン情報を確認できません。");
        if (authentication.getPrincipal() instanceof OidcUser user) {
            String preferred = user.getAttribute("preferred_username");
            if (preferred != null && !preferred.isBlank()) return preferred;
            if (user.getEmail() != null && !user.getEmail().isBlank()) return user.getEmail();
        }
        return authentication.getName();
    }
}
