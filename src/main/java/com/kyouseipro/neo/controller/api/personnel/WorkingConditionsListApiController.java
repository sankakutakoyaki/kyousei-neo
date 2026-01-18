package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.dto.IdRequest;
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
    @GetMapping("/api/working_conditions/get/list")
	@ResponseBody
    public List<WorkingConditionsListEntity> getList() {
        return workingConditionsListService.getList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/api/working_conditions/get/list/category")
	@ResponseBody
    public List<WorkingConditionsListEntity> getListByCategoryId(@RequestBody IdRequest req) {
        return workingConditionsListService.getListByCategoryId(req.getId());
    }
}
