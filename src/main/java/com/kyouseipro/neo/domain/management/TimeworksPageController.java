package com.kyouseipro.neo.domain.management;
import java.util.Map;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksPageController {
    private final OfficeService officeService;

    /** 勤怠打刻画面を返す。 */
    @GetMapping("/timeworks")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getTimeworks() {
        return "fragments/pages/management/timeworks/content :: content";
    }

    @GetMapping("/api/timeworks/init/cache")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public Map<String, Object> initTimeworks(Authentication authentication) {
        boolean canManage = authentication.getAuthorities().stream()
            .anyMatch(authority -> List.of(
                "APPROLE_admin", "APPROLE_master", "APPROLE_leader", "APPROLE_staff"
            ).contains(authority.getAuthority()));
        return Map.of(
            "common", Map.of(),
            "page", Map.of(
                "canManage", canManage,
                "officeComboList", canManage
                    ? officeService.findComboByCategory(CompanyCategory.OWN.getCode())
                    : List.of()
            )
        );
    }
}
