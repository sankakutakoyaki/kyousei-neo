package com.kyouseipro.neo.controller.api.corporation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.corporation.OfficeListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OfficeListApiController {
    private final OfficeListService officeListService;
    private final ComboBoxService comboBoxService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/office/get/list")
	@ResponseBody
    public List<OfficeListEntity> getList() {
        return officeListService.getList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/api/office/get/list/category")
	@ResponseBody
    public List<OfficeListEntity> getListByCategoryId(@RequestBody IdRequest req) {
        return officeListService.getListByCategoryId(req.getId());
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/office/get/combo")
	@ResponseBody
    public List<OfficeListEntity> getCombo() {
        return comboBoxService.getOfficeList();
    }
}
