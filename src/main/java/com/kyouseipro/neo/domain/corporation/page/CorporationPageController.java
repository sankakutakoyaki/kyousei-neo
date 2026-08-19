package com.kyouseipro.neo.domain.corporation.page;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.BloodType;
import com.kyouseipro.neo.common.enums.code.ClientCategory;
import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.EmployeeCategory;
import com.kyouseipro.neo.common.enums.code.Gender;
import com.kyouseipro.neo.common.enums.code.RecycleGroup;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.business.api.recycle.RecycleService;
import com.kyouseipro.neo.domain.corporation.api.company.CompanyService;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CorporationPageController {
    private final CompanyService companyService;
    private final OfficeService officeService;
    private final RecycleService recycleService;

    /**
	 * 取引先
	 * @param mv
	 * @return
	 */
	@GetMapping("/client")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getClient() {
        return "fragments/pages/corporation/client/content :: content";
    }

    /**
	 * パートナー
	 * @param mv
	 * @return
	 */
	@GetMapping("/partner")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getPartner() {
        return "fragments/pages/corporation/partner/content :: content";
    }

    /**
     * 製造業者
     * @return
     */
	@GetMapping("/recyclemaker")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getRecycleMaker() {
        return "fragments/pages/corporation/recyclemaker/content :: content";
    }

    @GetMapping("/api/client/init/cache")
    @ResponseBody
    public Map<String, Object> initClient() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class),
                "companyCategory", EnumUtil.toMap(CompanyCategory.class)
            ),
            "page", Map.of(
                "companyComboList", companyService.findComboClientAll(),
                "officeComboList", officeService.findComboClientAll(),
                "clientCategoryComboList", EnumUtil.toCombo(ClientCategory.class)
            )
        );
    }

    @GetMapping("/api/partner/init/cache")
    @ResponseBody
    public Map<String, Object> initPartner() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class),
                "companyCategory", EnumUtil.toMap(CompanyCategory.class),
                "employeeCategory", EnumUtil.toMap(EmployeeCategory.class)
            ),
            "page", Map.of(
                "companyComboList", companyService.findComboByCategory(CompanyCategory.PARTNER.getCode()),
                "genderComboList", EnumUtil.toCombo(Gender.class),
                "bloodTypeComboList", EnumUtil.toCombo(BloodType.class)
            )
        );
    }

    @GetMapping("/api/recyclemaker/init/cache")
    @ResponseBody
    public Map<String, Object> initRecycleMaker() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                "recyclemakerComboList", recycleService.findMakerCombo(),
                "groupComboList", EnumUtil.toCombo(RecycleGroup.class)
            )
        );
    }
}