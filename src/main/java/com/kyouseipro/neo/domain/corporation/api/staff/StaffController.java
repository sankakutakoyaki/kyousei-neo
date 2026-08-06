package com.kyouseipro.neo.domain.corporation.api.staff;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.combo.entity.ComboDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class StaffController {
    private final StaffService staffService;

    @GetMapping("/combo")
    @ResponseBody
    public List<ComboDto> clientCombo() {
        return staffService.findComboAll();
    }
}
