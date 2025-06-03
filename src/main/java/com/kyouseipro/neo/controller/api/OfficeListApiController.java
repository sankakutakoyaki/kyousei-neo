package com.kyouseipro.neo.controller.api;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.service.corporation.OfficeListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OfficeListApiController {
    private final OfficeListService officeListService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/office/get/list")
	@ResponseBody
    public List<OfficeListEntity> getEntityList() {
        return officeListService.getOfficeList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/office/get/list/category")
	@ResponseBody
    public List<OfficeListEntity> getEntityListByCategory(@RequestParam int category) {
        return officeListService.getOfficeListByCategory(category);
    }
}
