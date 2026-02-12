package com.kyouseipro.neo.corporation.company.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.corporation.company.entity.CompanyListEntity;
import com.kyouseipro.neo.corporation.company.service.CompanyListService;
import com.kyouseipro.neo.dto.IdRequest;

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
    public SimpleResponse<List<CompanyListEntity>> getList() {
        return new SimpleResponse<>(null, companyListService.getList());
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/client/get/list")
	@ResponseBody
    public SimpleResponse<List<CompanyListEntity>> getClientList() {
        return new SimpleResponse<>(null, companyListService.getClientList());
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/partner/get/list")
	@ResponseBody
    public SimpleResponse<List<CompanyListEntity>> getPartnerList() {
        return new SimpleResponse<>(null, companyListService.getPartnerList());
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/company/get/list/category")
	@ResponseBody
    public SimpleResponse<List<CompanyListEntity>> getListByCategoryId(@RequestBody IdRequest req) {
        return new SimpleResponse<>(null, companyListService.getListByCategoryId(req.getId()));
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/client/get/combo")
	@ResponseBody
    public SimpleResponse<List<SimpleData>> getClientCombo() {
        return new SimpleResponse<>(null, comboBoxService.getClientList());
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/partner/get/combo")
	@ResponseBody
    public SimpleResponse<List<SimpleData>> getPartnerCombo() {
        return new SimpleResponse<>(null, comboBoxService.getPartnerList());
    }
}
