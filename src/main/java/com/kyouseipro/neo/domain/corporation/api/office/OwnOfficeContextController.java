package com.kyouseipro.neo.domain.corporation.api.office;

import java.util.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import lombok.RequiredArgsConstructor;

@RestController @RequiredArgsConstructor
public class OwnOfficeContextController {
    private final SqlRepository sql;
    @GetMapping("/api/own-offices/context")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin','APPROLE_master','APPROLE_leader','APPROLE_staff','APPROLE_user')")
    public Map<String,Object> context(Authentication authentication) {
        String account=authentication.getName();
        if(authentication.getPrincipal() instanceof OidcUser user) {
            String preferred=user.getAttribute("preferred_username");
            if(preferred!=null && !preferred.isBlank())account=preferred;
            else if(user.getEmail()!=null && !user.getEmail().isBlank())account=user.getEmail();
        }
        var offices=sql.selectMap("SELECT o.office_id AS value,o.name AS label FROM offices o JOIN companies c ON c.company_id=o.company_id WHERE o.state=0 AND c.state=0 AND c.category=0 ORDER BY o.office_id",List.of());
        var self=sql.selectMap("SELECT DISTINCT office_id FROM employees WHERE state=0 AND account=?",List.of(account));
        Object own=self.size()==1?self.get(0).get("officeId"):null;
        Object chosen=own;
        if(own==null || offices.stream().noneMatch(o->Objects.toString(o.get("value"),"").equals(chosen.toString())))own="";
        boolean headOffice="1000".equals(own.toString());
        return Map.of("offices",offices,"defaultOfficeId",own,"isHeadOffice",headOffice);
    }
}
