package com.kyouseipro.neo.domain.management;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.ShiftType;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeShiftPageController {

    private final OfficeService officeService;

    /**
     * 従業員シフト
     */
    @GetMapping("/employeeshifts")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getEmployeeShifts() {
        return "fragments/pages/management/employeeshift/content :: content";
    }

    /**
     * 初期キャッシュ
     */
    @GetMapping("/api/employeeshift/init/cache")
    @ResponseBody
    public Map<String, Object> initEmployeeShift() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                "officeComboList", officeService.findComboByCategory(CompanyCategory.OWN.getCode()),
                "shiftTypeComboList", EnumUtil.toCombo(ShiftType.class)
            )
        );
    }
}