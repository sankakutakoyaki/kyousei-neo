package com.kyouseipro.neo.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.TimeworksListEntity;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.TimeworksListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TimeworksListApiController {
    private final TimeworksListService timeworksListService;
    private final EmployeeService employeeService;

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/timeworks/get/today")
	@ResponseBody
    public List<TimeworksListEntity> getEntityList() {
        return timeworksListService.getTodaysList();
    }

    /**
     * EntityListを取得する
     * @return
     */
    @PostMapping("/timeworks/get/today/id")
	@ResponseBody
    public TimeworksListEntity getTodaysEntityByEmployeeId(@RequestParam int id) {
        TimeworksListEntity entity = timeworksListService.getTodaysEntityByEmployeeId(id);
        if (entity == null) {
            entity = new TimeworksListEntity();
            EmployeeEntity emp = employeeService.getEmployeeById(id);
            if (emp != null) {
                entity.setEmployee_id(emp.getEmployee_id());
                entity.setFull_name(emp.getFull_name());
                entity.setWork_date(LocalDate.now());
            } else {
                return entity;
            }
        }
        return entity;
    }

    /**
     * 勤怠情報を作成・更新する
     * @param timeworksEntity
     * @return
     */
    @PostMapping("/timeworks/regist/today")
	@ResponseBody
    public Integer saveTimeworksToday(@RequestBody TimeworksListEntity timeworksEntity, @AuthenticationPrincipal OidcUser principal) {
        // LocalDate date = LocalDate.now();
        // String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"));
        String editor = principal.getAttribute("preferred_username");
        return timeworksListService.saveTodaysTimeworks(timeworksEntity, editor);
    }
}
