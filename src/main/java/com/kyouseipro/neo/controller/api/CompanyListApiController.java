package com.kyouseipro.neo.controller.api;

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
    @GetMapping("/company/get/list")
	@ResponseBody
    public List<CompanyListEntity> getEntityList() {
        return companyListService.getCompanyList();
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/client/get/list")
	@ResponseBody
    public List<CompanyListEntity> getEntityListForClient() {
        return companyListService.getClientList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/company/get/list/category")
	@ResponseBody
    public List<CompanyListEntity> getEntityListByCategory(@RequestParam int category) {
        return companyListService.getCompanyListByCategory(category);
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/client/get/combo")
	@ResponseBody
    public List<SimpleData> getComboForClient() {
        return comboBoxService.getClientList();
    }
}
