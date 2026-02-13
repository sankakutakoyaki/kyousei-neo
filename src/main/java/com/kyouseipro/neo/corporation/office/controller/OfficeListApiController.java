package com.kyouseipro.neo.corporation.office.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.corporation.office.entity.OfficeListEntity;
import com.kyouseipro.neo.corporation.office.service.OfficeListService;
import com.kyouseipro.neo.dto.IdRequest;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/office")
public class OfficeListApiController {
    private final OfficeListService officeListService;
    private final ComboBoxService comboBoxService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/list")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<OfficeListEntity>>> getList() {
        return ResponseEntity.ok(SimpleResponse.ok(officeListService.getList()));
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/client/list")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<OfficeListEntity>>> getClientList() {
        return ResponseEntity.ok(SimpleResponse.ok(officeListService.getClientList()));
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/get/list/category")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<OfficeListEntity>>> getListByCategoryId(@RequestBody IdRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(officeListService.getListByCategoryId(req.getId())));
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/combo")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<OfficeListEntity>>> getCombo() {
        return ResponseEntity.ok(SimpleResponse.ok(comboBoxService.getOfficeList()));
    }
}
