package com.kyouseipro.neo.pages;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.abstracts.BaseController;
import com.kyouseipro.neo.common.enums.code.BloodType;
import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.EmployeeCategory;
import com.kyouseipro.neo.common.enums.code.Gender;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.corporation.office.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PersonnelPageController extends BaseController {
    private final OfficeService officeService;

    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/employee")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getEmployee() {
        return "fragments/pages/personnel/employee/content :: content";
    }

    @GetMapping("/api/employee/init/cache")
    @ResponseBody
    public Map<String, Object> initEmployee() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class),
                "employeeCategory", EnumUtil.toMap(EmployeeCategory.class)
            ),
            "page", Map.of(
                "officeComboList", officeService.findComboByCategory(CompanyCategory.OWN.getCode()),
                "genderComboList", EnumUtil.toCombo(Gender.class),
                "bloodTypeComboList", EnumUtil.toCombo(BloodType.class)
            )
        );
    }
}