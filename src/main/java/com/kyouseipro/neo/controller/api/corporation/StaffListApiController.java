package com.kyouseipro.neo.controller.api.corporation;

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
    @GetMapping("/api/staff/get/list")
	@ResponseBody
    public List<StaffListEntity> getList() {
            return staffListService.getList();
    }
}
