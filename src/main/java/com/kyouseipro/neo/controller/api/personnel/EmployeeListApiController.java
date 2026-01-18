package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.service.personnel.EmployeeListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeListApiController {
    private final EmployeeListService employeeListService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/api/employee/get/list")
	@ResponseBody
    public List<EmployeeListEntity> getList() {
        return employeeListService.getList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/api/employee/get/list/category")
	@ResponseBody
    // public List<EmployeeListEntity> getListByCategoryId(@RequestParam int category) {
    public List<EmployeeListEntity> getListByCategoryId(@RequestBody IdRequest req) {
        return employeeListService.getListByCategoryId(req.getId());
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/api/employee/get/list/{type}")
	@ResponseBody
    public List<EmployeeListEntity> getListByCategoryId(@PathVariable String type) {
        int category = 0;

        switch (type) {
            case "partner":
                category = Enums.clientCategory.PARTNER.getCode();
                break;
        
            default:
                break;
        }
        return employeeListService.getListByCategoryId(category);
    }
}
