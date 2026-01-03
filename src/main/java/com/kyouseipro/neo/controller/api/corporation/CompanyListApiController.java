package com.kyouseipro.neo.controller.api.corporation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.corporation.CompanyListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyListApiController {
    private final CompanyListService companyListService;
    private final ComboBoxService comboBoxService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/company/get/list")
	@ResponseBody
    public List<CompanyListEntity> getList() {
        return companyListService.getList();
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/client/get/list")
	@ResponseBody
    public List<CompanyListEntity> getClientList() {
        return companyListService.getClientList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/api/company/get/list/category")
	@ResponseBody
    public List<CompanyListEntity> getListByCategoryId(@RequestParam int category) {
        return companyListService.getListByCategoryId(category);
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/client/get/combo")
	@ResponseBody
    public List<SimpleData> getClientCombo() {
        return comboBoxService.getClientList();
    }
}
