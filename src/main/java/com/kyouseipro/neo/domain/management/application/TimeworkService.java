package com.kyouseipro.neo.domain.management.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.management.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.model.TimeworkStatus;
import com.kyouseipro.neo.domain.management.model.TimeworkPeriod;
import com.kyouseipro.neo.domain.management.model.TimeworkUpdateRequest;
import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.domain.management.repository.TimeworkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworkService {
    private final TimeworkRepository repository;

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

    public List<TimeworkListItem> findTodayList() {
        return repository.findList(LocalDate.now(), null, null);
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
        if (request.startTime() == null) throw new BusinessException("出勤時刻を入力してください。");
        if (request.endTime() != null && request.endTime().isBefore(request.startTime())) {
            throw new BusinessException("退勤時刻は出勤時刻以降を入力してください。");
        }
        repository.updateTimes(
            request.timeworkId(), request.startTime(), request.endTime(), request.version(), account(authentication)
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

    @Transactional
    public TimeworkStatus stampSelf(Authentication authentication, String stampType) {
        TimeworkStatus self = findSelf(authentication);
        return stamp(authentication, self.employeeId(), stampType);
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
