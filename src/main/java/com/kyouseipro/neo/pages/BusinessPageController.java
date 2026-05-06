package com.kyouseipro.neo.pages;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.abstracts.BaseController;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.enums.code.RecycleCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.business.recycle.RecycleService;
import com.kyouseipro.neo.domain.corporation.company.CompanyService;
import com.kyouseipro.neo.domain.corporation.office.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BusinessPageController extends BaseController {
    private final RecycleService recycleService;
    private final CompanyService companyService;
    private final OfficeService officeService;

	@GetMapping("/recycle")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getRecycle() {
        return "fragments/pages/business/recycle/content :: content";
    }

    @GetMapping("/api/recycle/init/cache")
    @ResponseBody
    public Map<String, Object> initRecycle() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                "recycleItemComboList", recycleService.findItemCombo(),
                "recycleCategoryComboList", EnumUtil.toCombo(RecycleCategory.class),
                "clientComboList", companyService.findComboByCategory(Enums.clientCategory.SHIPPER.getCode()),
                "officeComboList", officeService.findComboClientAll()
            )
        );
    }
}
