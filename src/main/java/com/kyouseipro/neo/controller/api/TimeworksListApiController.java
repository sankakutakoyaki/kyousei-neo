package com.kyouseipro.neo.controller.api;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.service.personnel.TimeworksListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksListApiController {
    private final TimeworksListService timeworksListService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/timeworks/get/today")
	@ResponseBody
    public List<TimeworksListEntity> getEntityList() {
        return timeworksListService.getTodaysList();
    }
}
