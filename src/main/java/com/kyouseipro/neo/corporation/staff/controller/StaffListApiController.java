package com.kyouseipro.neo.corporation.staff.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.corporation.staff.entity.StaffListEntity;
import com.kyouseipro.neo.corporation.staff.service.StaffListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/staff")
public class StaffListApiController {
    private final StaffListService staffListService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/get/list")
	@ResponseBody
    public ResponseEntity<SimpleResponse<List<StaffListEntity>>> getList() {
            return ResponseEntity.ok(SimpleResponse.ok(staffListService.getList()));
    }
}
