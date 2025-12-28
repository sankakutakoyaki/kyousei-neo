package com.kyouseipro.neo.controller.api.ks;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.ks.KsSalesEntity;
import com.kyouseipro.neo.service.ks.KsSalesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KsSalesApiController {
    private final KsSalesService ksSalesService;

    /**
     * 期間内の情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/ks/sales/get/between/{type}")
	@ResponseBody
    public List<KsSalesEntity> getBetweenAllEntity(@RequestParam LocalDate start, @RequestParam LocalDate end, @PathVariable String type) {
        List<KsSalesEntity> list = ksSalesService.getBetweenKsSalesEntity(start, end, type);
        return list;
    }
}
