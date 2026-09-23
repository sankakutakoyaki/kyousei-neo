package com.kyouseipro.neo.domain.corporation.api.office;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnOfficeContextService {

    private final SqlRepository sql;

    /**
     * ログインユーザーのアカウントを取得
     */
    public String getAccount(Authentication authentication) {
        String account = authentication.getName();
        if(authentication.getPrincipal() instanceof OidcUser user){
            String preferred = user.getAttribute("preferred_username");
            if(preferred != null && !preferred.isBlank()){
                return preferred;
            }
            if(user.getEmail() != null && !user.getEmail().isBlank()){
                return user.getEmail();
            }
        }
        return account;
    }

    /**
     * ログインユーザーの所属営業所IDを取得
     *
     * 複数または未登録の場合は null
     */
    public Integer getOfficeId(Authentication authentication) {
        String account = getAccount(authentication);
        var rows = sql.selectMap(
                """
                SELECT DISTINCT
                    office_id
                FROM employees
                WHERE state = 0
                  AND account = ?
                """,
                List.of(account)
            );

        if(rows.size() != 1){
            return null;
        }

        Object value = rows.get(0).get("officeId");
        if(value == null){
            return null;
        }

        return ((Number) value).intValue();
    }
}