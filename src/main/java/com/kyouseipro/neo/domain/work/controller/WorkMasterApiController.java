package com.kyouseipro.neo.domain.work.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kyouseipro.neo.common.request.StringRequest;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.work.entity.WorkMasterDto;
import com.kyouseipro.neo.domain.work.service.WorkMasterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/work")
public class WorkMasterApiController {

    private final WorkMasterService workMasterService;

    @PostMapping("/get/workcode")
    public ResponseEntity<SimpleResponse<WorkMasterDto>> getByWorkCode(@RequestBody StringRequest req) {
        WorkMasterDto dto = workMasterService.findByWorkCode(req.getValue());
        return ResponseEntity.ok(SimpleResponse.ok(dto));
    }
}