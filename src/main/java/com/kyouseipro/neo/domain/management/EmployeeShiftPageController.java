package com.kyouseipro.neo.domain.management;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.EmployeeCategory;
import com.kyouseipro.neo.common.enums.code.ShiftType;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.common.holiday.HolidayService;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.domain.personnel.employee.EmployeeWorkCategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeShiftPageController {

    private final OfficeService officeService;
    private final OwnOfficeContextService ownOfficeContextService;
    private final EmployeeWorkCategoryService employeeWorkCategoryService;
    private final HolidayService holidayService;

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
    public Map<String, Object> initEmployeeShift(Authentication authentication) {
        Integer loginOfficeId = ownOfficeContextService.getOfficeId(authentication);
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class),
                "companyCategory", EnumUtil.toMap(CompanyCategory.class),
                "employeeCategory", EnumUtil.toMap(EmployeeCategory.class)
            ),
            "page", Map.of(
                "officeComboList", officeService.findComboByCategory(CompanyCategory.OWN.getCode()),
                "shiftTypeComboList", EnumUtil.toCombo(ShiftType.class),
                "employeeWorkCategoryComboList", employeeWorkCategoryService.findCombo(),
                "loginOfficeId", loginOfficeId != null ? loginOfficeId : "",
                "holidays", holidayService.getHolidays(LocalDate.now().getYear())
            )
        );
    }

    /**
     * 指定年の祝日一覧
     */
    @GetMapping("/api/employeeshift/holidays")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public Map<String, String> getHolidays(@RequestParam int year) {
        if(year < 1955 || year > 2100){
            throw new IllegalArgumentException("対象年が不正です");
        }
        return holidayService.getHolidays(year);
    }
}