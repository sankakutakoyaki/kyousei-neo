package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.personnel.WorkingConditionsListEntity;
import com.kyouseipro.neo.service.personnel.WorkingConditionsListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsListApiController {
    private final WorkingConditionsListService workingConditionsListService;
    
    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/working_conditions/get/list")
	@ResponseBody
    public List<WorkingConditionsListEntity> getList() {
        return workingConditionsListService.getList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/working_conditions/get/list/category")
	@ResponseBody
    public List<WorkingConditionsListEntity> getListByCategoryId(@RequestParam int category) {
        return workingConditionsListService.getListByCategoryId(category);
    }
}
