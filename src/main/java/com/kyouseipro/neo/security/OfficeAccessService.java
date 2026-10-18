package com.kyouseipro.neo.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfficeAccessService {

    private final OwnOfficeContextService ownOfficeContextService;
    private final DataScopeService dataScopeService;

    public boolean canAccessAllOffices(Authentication authentication) {
        return "ALL".equals(dataScopeService.getScope(authentication));
    }

    public boolean canAccessOffice(Authentication authentication, Integer officeId) {
        if (officeId == null) return false;

        String scope = dataScopeService.getScope(authentication);
        if ("ALL".equals(scope)) return true;

        if ("OWN".equals(scope) || "OWN_ASSIGNED".equals(scope)) {
            Integer ownOfficeId = ownOfficeContextService.getOfficeId(authentication);
            return ownOfficeId != null && ownOfficeId.equals(officeId);
        }

        return false;
    }

    public Integer getRestrictedOfficeId(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("ログイン情報を確認できません。");
        }

        String scope = dataScopeService.getScope(authentication);
        if ("ALL".equals(scope)) return null;

        if ("OWN".equals(scope) || "OWN_ASSIGNED".equals(scope)) {
            Integer officeId = ownOfficeContextService.getOfficeId(authentication);
            if (officeId != null) return officeId;
        }

        throw new AccessDeniedException("アクセス可能な営業所を確認できません。");
    }
}