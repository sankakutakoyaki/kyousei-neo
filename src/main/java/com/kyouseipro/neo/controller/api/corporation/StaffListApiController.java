package com.kyouseipro.neo.controller.api.corporation;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.service.corporation.StaffListService;

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
    public List<StaffListEntity> getList() {
            return staffListService.getList();
    }
}
