package com.kyouseipro.neo.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderAccessService {

    private final DataScopeService dataScopeService;
    private final OwnOfficeContextService ownOfficeContextService;

    public record AccessCondition(
        String scope,
        Integer officeId,
        Long employeeId
    ) {}

    public AccessCondition getAccessCondition(Authentication authentication) {
        String scope = dataScopeService.getScope(authentication);

        if ("ALL".equals(scope)) {
            return new AccessCondition("ALL", null, null);
        }

        if (scope == null || !java.util.Set.of(
                "OWN", "ASSIGNED", "OWN_ASSIGNED").contains(scope)) {
            throw new AccessDeniedException("受注の参照権限がありません。");
        }

        Integer officeId = null;
        Long employeeId = null;

        if ("OWN".equals(scope) || "OWN_ASSIGNED".equals(scope)) {
            officeId = ownOfficeContextService.getOfficeId(authentication);
        }

        if ("ASSIGNED".equals(scope) || "OWN_ASSIGNED".equals(scope)) {
            employeeId = ownOfficeContextService.getEmployeeId(authentication);
        }

        if ("OWN".equals(scope) && officeId == null) {
            throw new AccessDeniedException("所属営業所を確認できません。");
        }

        if ("ASSIGNED".equals(scope) && employeeId == null) {
            throw new AccessDeniedException("従業員情報を確認できません。");
        }

        if ("OWN_ASSIGNED".equals(scope)
                && officeId == null && employeeId == null) {
            throw new AccessDeniedException("アクセス可能な受注を確認できません。");
        }

        return new AccessCondition(scope, officeId, employeeId);
    }
}