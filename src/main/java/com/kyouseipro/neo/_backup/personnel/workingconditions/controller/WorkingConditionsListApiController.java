package com.kyouseipro.neo._backup.personnel.workingconditions.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo._backup.dto.NumberRequest;
import com.kyouseipro.neo._backup.personnel.workingconditions.entity.WorkingConditionsListEntity;
import com.kyouseipro.neo._backup.personnel.workingconditions.service.WorkingConditionsListService;
import com.kyouseipro.neo.common.response.SimpleResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/working_conditions")
public class WorkingConditionsListApiController {
    private final WorkingConditionsListService workingConditionsListService;
    
    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/list")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<WorkingConditionsListEntity>>> getList() {
        return ResponseEntity.ok(SimpleResponse.ok(workingConditionsListService.getList()));
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/get/list/category")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<WorkingConditionsListEntity>>> getListByCategoryId(@RequestBody NumberRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(workingConditionsListService.getListByCategoryId(req.getNumber())));
    }
}
