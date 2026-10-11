package com.kyouseipro.neo.domain.business.page;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.ArrivalState;
import com.kyouseipro.neo.common.enums.code.ClientCategory;
import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.OrderCategory;
import com.kyouseipro.neo.common.enums.code.RecycleCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.business.recycle.application.RecycleService;
import com.kyouseipro.neo.domain.corporation.api.company.CompanyService;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.domain.corporation.api.staff.StaffService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BusinessPageController {
    private final RecycleService recycleService;
    private final CompanyService companyService;
    private final OfficeService officeService;
    private final StaffService staffService;
    private final OwnOfficeContextService ownOfficeContextService;

	@GetMapping("/order")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getOrder() {
        return "fragments/pages/business/order/content :: content";
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getProducts() {
        return "fragments/pages/business/order/products :: content";
    }

    @GetMapping("/api/order/init/cache")
    @ResponseBody
    public Map<String, Object> initOrder(Authentication authentication) {
        Integer loginOfficeId = ownOfficeContextService.getOfficeId(authentication);
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                "categoryComboList", EnumUtil.toCombo(OrderCategory.class),
                "arrivalStateComboList", EnumUtil.toCombo(ArrivalState.class),
                "clientComboList", companyService.findComboByCategory(ClientCategory.SHIPPER.getCode()),
                "officeComboList", officeService.findComboClientAll(),
                "ownOfficeComboList", officeService.findComboByCategory(CompanyCategory.OWN.getCode()),
                "staffComboList", staffService.findComboAll(),
                "loginOfficeId", loginOfficeId != null ? loginOfficeId : ""
            )
        );
    }

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
                "state", EnumUtil.toMap(State.class),
                "recycleCategory", EnumUtil.toMap(RecycleCategory.class)
            ),
            "page", Map.of(
                "recycleItemComboList", recycleService.findItemCombo(),
                "recycleCategoryComboList", EnumUtil.toCombo(RecycleCategory.class),
                "clientComboList", companyService.findComboByCategory(ClientCategory.SHIPPER.getCode()),
                "disposalComboList", companyService.findComboByCategory(ClientCategory.FACILITY.getCode()),
                "officeComboList", officeService.findComboClientAll()
            )
        );
    }
}
