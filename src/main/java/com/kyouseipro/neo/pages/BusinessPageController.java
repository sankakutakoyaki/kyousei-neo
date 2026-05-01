package com.kyouseipro.neo.pages;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.abstracts.BaseController;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BusinessPageController extends BaseController {


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
            //     "companyCategory", EnumUtil.toMap(CompanyCategory.class)
            // ),
            // "page", Map.of(
            //     "companyComboList", companyService.findComboClientAll(),
            //     "officeComboList", officeService.findComboClientAll(),
            //     "clientCategoryComboList", EnumUtil.toCombo(ClientCategory.class)
            )
        );
    }
}
