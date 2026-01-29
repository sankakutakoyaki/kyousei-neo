package com.kyouseipro.neo.controller.api.ks;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.BetweenRequest;
import com.kyouseipro.neo.entity.ks.KsSalesEntity;
import com.kyouseipro.neo.service.ks.KsSalesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/ks")
public class KsSalesApiController {
    private final KsSalesService ksSalesService;

    /**
     * 期間内の情報を取得する
     * @param start
     * @param end
     * @return
     */
    @PostMapping("/sales/get/between/{type}")
	@ResponseBody
    public List<KsSalesEntity> getAllFromBetween(@RequestBody BetweenRequest req) {
        List<KsSalesEntity> list = ksSalesService.getAllFromBetween(req.getStart(), req.getEnd(), req.getType());
        return list;
    }
}
