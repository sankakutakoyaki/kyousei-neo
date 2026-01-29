package com.kyouseipro.neo.controller.api.corporation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.CompanyListEntity;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.corporation.CompanyListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class CompanyListApiController {
    private final CompanyListService companyListService;
    private final ComboBoxService comboBoxService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/company/get/list")
	@ResponseBody
    public List<CompanyListEntity> getList() {
        return companyListService.getList();
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/client/get/list")
	@ResponseBody
    public List<CompanyListEntity> getClientList() {
        return companyListService.getClientList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/company/get/list/category")
	@ResponseBody
    public List<CompanyListEntity> getListByCategoryId(@RequestBody IdRequest req) {
        return companyListService.getListByCategoryId(req.getId());
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/client/get/combo")
	@ResponseBody
    public List<SimpleData> getClientCombo() {
        return comboBoxService.getClientList();
    }
}
