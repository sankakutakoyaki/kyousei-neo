package com.kyouseipro.neo.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.interfaceis.HistoryTarget;
import com.kyouseipro.neo.service.dto.HistoryService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class HistoryAspect {

    private final HistoryService historyService;

    @Around("@annotation(history)")
    public Object recordHistory(
            ProceedingJoinPoint pjp,
            HistoryTarget history
    ) throws Throwable {

        String userName = getUserName();

        try {
            Object result = pjp.proceed();

            // ✅ 成功時
            historyService.save(
                userName,
                history.table(),
                history.action(),
                200,
                "成功"
            );

            return result;

        } catch (BusinessException e) {

            // ❌ 業務エラー
            historyService.save(
                userName,
                history.table(),
                history.action(),
                e.getResultCode(),
                e.getMessage()
            );
            throw e;

        } catch (Exception e) {

            // ❌ 想定外エラー
            historyService.save(
                userName,
                history.table(),
                history.action(),
                500,
                "システムエラー"
            );
            throw e;
        }
    }

    private String getUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "unknown";
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof OidcUser user) {
            return user.getAttribute("preferred_username");
        }
        return auth.getName();
    }
}