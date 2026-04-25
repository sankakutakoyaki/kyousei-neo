package com.kyouseipro.neo.domain.corporation.office;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.master.combo.ComboDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/office")
public class OfficeController {
    private final OfficeService officeService;

    @GetMapping("/client/combo")
    @ResponseBody
    public List<ComboDto> clientCombo() {
        return officeService.findComboClientAll();
    }
}
