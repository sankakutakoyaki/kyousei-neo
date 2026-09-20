package com.kyouseipro.neo.domain.operations.dispatch;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.DispatchCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DispatchPageController {

    private final OfficeService officeService;
    private final OwnOfficeContextService ownOfficeContextService;

    @GetMapping("/dispatch")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getDispatch() {
        return "fragments/pages/operations/dispatch/content :: content";
    }

    @GetMapping("/api/dispatch/init/cache")
    @ResponseBody
    public Map<String, Object> initDispatch(
        Authentication authentication
    ) {

        Integer loginOfficeId =
            ownOfficeContextService
                .getOfficeId(authentication);

        return Map.of(
            "common", Map.of(
                "state",
                EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                "officeComboList",
                officeService.findComboByCategory(
                    CompanyCategory.OWN.getCode()
                ),
                "dispatchCategoryComboList",
                EnumUtil.toCombo(
                    DispatchCategory.class
                ),
                "loginOfficeId",
                loginOfficeId != null
                    ? loginOfficeId
                    : ""
            )
        );
    }
}