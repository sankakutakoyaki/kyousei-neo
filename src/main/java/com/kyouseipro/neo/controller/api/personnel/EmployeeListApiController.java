package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @GetMapping("/employee/get/list")
	@ResponseBody
    public List<EmployeeListEntity> getEntityList() {
        return employeeListService.getEmployeeList();
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/employee/get/list/category")
	@ResponseBody
    public List<EmployeeListEntity> getEntityListByCategory(@RequestParam int category) {
        return employeeListService.getEmployeeListByCategory(category);
    }
}
