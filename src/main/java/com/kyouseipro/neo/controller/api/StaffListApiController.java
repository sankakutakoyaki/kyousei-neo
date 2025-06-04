package com.kyouseipro.neo.controller.api;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.service.corporation.StaffListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StaffListApiController {
    private final StaffListService staffListService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/staff/get/list")
	@ResponseBody
    public List<StaffListEntity> getEntityList() {
            return staffListService.getStaffList();
    }
}
