package com.kyouseipro.neo.personnel.workingconditions.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsListEntity;
import com.kyouseipro.neo.personnel.workingconditions.service.WorkingConditionsListService;

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
    public SimpleResponse<List<WorkingConditionsListEntity>> getList() {
        return new SimpleResponse<>(null, workingConditionsListService.getList());
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/get/list/category")
	@ResponseBody
    public SimpleResponse<List<WorkingConditionsListEntity>> getListByCategoryId(@RequestBody IdRequest req) {
        return new SimpleResponse<>(null, workingConditionsListService.getListByCategoryId(req.getId()));
    }
}
