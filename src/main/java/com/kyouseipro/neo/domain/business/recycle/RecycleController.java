package com.kyouseipro.neo.domain.business.recycle;

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
    private final RecycleService recycleService;

    @GetMapping("/item/combo")
    @ResponseBody
    public List<ComboDto> itemCombo() {
        return recycleService.findItemCombo();
    }
}
