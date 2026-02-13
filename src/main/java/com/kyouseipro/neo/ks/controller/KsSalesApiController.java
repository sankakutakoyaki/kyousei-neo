package com.kyouseipro.neo.ks.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.BetweenRequest;
import com.kyouseipro.neo.ks.entity.KsSalesEntity;
import com.kyouseipro.neo.ks.service.KsSalesService;

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
    public ResponseEntity<SimpleResponse<List<KsSalesEntity>>> getAllFromBetween(@RequestBody BetweenRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(ksSalesService.getAllFromBetween(req.getStart(), req.getEnd(), req.getType())));
    }
}
