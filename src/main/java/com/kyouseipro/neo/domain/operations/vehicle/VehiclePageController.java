package com.kyouseipro.neo.domain.operations.vehicle;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.code.Transmission;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.corporation.api.office.OfficeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VehiclePageController {

    private final OfficeService officeService;

    @GetMapping("/vehicles")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getVehicles() {
        return "fragments/pages/operations/vehicle/content :: content";
    }

    @GetMapping("/api/vehicle/init/cache")
    @ResponseBody
    public Map<String, Object> initVehicle() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                "officeComboList",
                    officeService.findComboByCategory(CompanyCategory.OWN.getCode()),
                "transmissionComboList",
                    EnumUtil.toCombo(Transmission.class)
            )
        );
    }
}