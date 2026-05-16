package com.kyouseipro.neo._backup.personnel.employee.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo._backup.Enums;
import com.kyouseipro.neo._backup.dto.NumberRequest;
import com.kyouseipro.neo._backup.personnel.employee.entity.EmployeeListEntity;
import com.kyouseipro.neo._backup.personnel.employee.service.EmployeeListService;
import com.kyouseipro.neo.common.response.SimpleResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeListApiController {
    private final EmployeeListService employeeListService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/list")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<EmployeeListEntity>>> getList() {
        return ResponseEntity.ok(SimpleResponse.ok(employeeListService.getList()));
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/get/list/category")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<EmployeeListEntity>>> getListByCategoryId(@RequestBody NumberRequest req) {
        return ResponseEntity.ok(SimpleResponse.ok(employeeListService.getListByCategoryId(req.getNumber())));
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @GetMapping("/get/list/{type}")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<EmployeeListEntity>>> getListByCategoryId(@PathVariable String type) {
        int category = 0;

        switch (type) {
            case "partner":
                category = Enums.employeeCategory.CONSTRUCT.getCode();
                break;
        
            default:
                break;
        }
        return ResponseEntity.ok(SimpleResponse.ok(employeeListService.getListByCategoryId(category)));
    }
}
