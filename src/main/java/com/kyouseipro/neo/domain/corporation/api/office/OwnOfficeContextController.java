package com.kyouseipro.neo.domain.corporation.api.office;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.common.combo.entity.ComboDto;
import com.kyouseipro.neo.common.enums.code.CompanyCategory;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OwnOfficeContextController {

    private final OfficeService officeService;
    private final OwnOfficeContextService ownOfficeContextService;


    @GetMapping("/api/own-offices/context")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin','APPROLE_master','APPROLE_leader','APPROLE_staff','APPROLE_user')")
    public Map<String, Object> context(
        Authentication authentication
    ) {

        List<ComboDto> offices =
            officeService.findComboByCategory(
                CompanyCategory.OWN.getCode()
            );

        Integer officeId =
            ownOfficeContextService
                .getOfficeId(authentication);

        boolean exists =
            offices.stream().anyMatch(
                office ->
                    Objects.equals(
                        office.getValue(),
                        officeId == null
                            ? null
                            : officeId.longValue()
                    )
            );

        Object defaultOfficeId =
            officeId != null && exists
                ? officeId
                : "";

        boolean headOffice =
            Objects.equals(
                officeId,
                1000
            );

        return Map.of(
            "offices",
            offices,
            "defaultOfficeId",
            defaultOfficeId,
            "isHeadOffice",
            headOffice
        );
    }
}