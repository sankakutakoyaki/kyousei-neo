package com.kyouseipro.neo.domain.corporation.company;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.master.combo.ComboDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/partner/combo")
    @ResponseBody
    public List<ComboDto> companyCombo() {
        return companyService.findComboByCategory(CompanyCategory.PARTNER.getCode());
    }
}
