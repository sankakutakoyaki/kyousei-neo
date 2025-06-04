package com.kyouseipro.neo.controller.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsApiController {
    private final WorkingConditionsService workingConditionsService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/working_conditions/get/id")
	@ResponseBody
    public WorkingConditionsEntity getEntityById(@RequestParam int id) {
            return workingConditionsService.getWorkingConditionsById(id);
    }

    /**
     * 情報を保存する
     * @param ENTITY
     * @return 
     */
    @PostMapping("/working_conditions/save")
	@ResponseBody
    public Integer saveEntity(@RequestBody WorkingConditionsEntity entity, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return workingConditionsService.saveWorkingConditions(entity, userName);
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/working_conditions/delete")
	@ResponseBody
    public WorkingConditionsEntity deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return workingConditionsService.deleteWorkingConditionsByIds(ids, userName);
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/working_conditions/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return workingConditionsService.downloadCsvWorkingConditionsByIds(ids, userName);
    }
}