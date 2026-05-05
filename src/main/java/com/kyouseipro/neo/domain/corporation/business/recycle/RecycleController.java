package com.kyouseipro.neo.domain.corporation.business.recycle;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.master.combo.ComboDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/recycle")
public class RecycleController {

    // @GetMapping("/maker/combo")
    // @ResponseBody
    // public List<ComboDto> partnerCombo() {
    //     return companyService.findComboByCategory(CompanyCategory.PARTNER.getCode());
    // }
    
}
