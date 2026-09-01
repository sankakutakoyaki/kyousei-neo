package com.kyouseipro.neo.domain.management.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.management.model.TimeworkListItem;
import com.kyouseipro.neo.domain.management.model.TimeworkStatus;
import com.kyouseipro.neo.domain.management.repository.TimeworkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeworkService {
    private static final List<String> MANAGEMENT_AUTHORITIES = List.of(
        "APPROLE_admin", "APPROLE_master", "APPROLE_leader", "APPROLE_staff"
    );
    private final TimeworkRepository repository;

    public TimeworkStatus findToday(Authentication authentication) {
        return requireStatus(authentication, LocalDate.now());
    }

    public List<TimeworkListItem> findList(Authentication authentication, LocalDate workDate, Long officeId) {
        LocalDate targetDate = workDate == null ? LocalDate.now() : workDate;
        TimeworkStatus currentUser = requireStatus(authentication, LocalDate.now());
        boolean canManage = authentication.getAuthorities().stream()
            .anyMatch(authority -> MANAGEMENT_AUTHORITIES.contains(authority.getAuthority()));
        return repository.findList(targetDate, canManage ? officeId : null, canManage ? null : currentUser.employeeId());
    }

    @Transactional
    public TimeworkStatus stamp(Authentication authentication, String stampType) {
        String type = stampType == null ? "" : stampType.trim().toUpperCase(Locale.ROOT);
        if (!type.equals("START") && !type.equals("END")) throw new BusinessException("打刻種別が不正です。");

        LocalDateTime now = LocalDateTime.now();
        String account = account(authentication);
        TimeworkStatus status = requireStatus(authentication, now.toLocalDate());
        if (type.equals("START")) {
            if (!status.canStart()) throw new BusinessException("本日はすでに出勤打刻されています。");
            repository.insertStart(status.employeeId(), status.officeId(), now.toLocalDate(), now, account);
        } else {
            if (!status.canEnd()) throw new BusinessException("退勤できる勤務状態ではありません。");
            repository.updateEnd(status.timeworkId(), now, account);
        }
        return requireStatus(authentication, now.toLocalDate());
    }

    private TimeworkStatus requireStatus(Authentication authentication, LocalDate workDate) {
        TimeworkStatus status = repository.findTodayByAccount(account(authentication), workDate);
        if (status == null) throw new BusinessException("ログインユーザーに対応する社員が登録されていません。");
        return status;
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
