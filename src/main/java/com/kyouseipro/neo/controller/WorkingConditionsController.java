package com.kyouseipro.neo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.WorkingConditionsEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WorkingConditionsController {
    private final EmployeeService employeeService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;
    private final WorkingConditionsService workingConditionsService;
    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/working_conditions")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getWorkingConditions(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "勤務条件");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/working_conditions :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/working_conditions.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new WorkingConditionsEntity());

        // 初期表示用従業員リスト取得
        List<IEntity> origin = workingConditionsService.getWorkingConditionsList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<IEntity> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<IEntity> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getNum());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getNum());

        // 履歴保存
        databaseService.saveHistory(userName, "working_conditions", "閲覧", 200, "");
		
        return mv;
    }

    /**
     * IDから従業員情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/working_conditions/get/id")
	@ResponseBody
    public IEntity getWorkingConditionsById(@RequestParam int id) {
        return workingConditionsService.getWorkingConditionsById(id);
    }

    /**
     * すべての従業員情報を取得する
     * @return
     */
    @GetMapping("/working_conditions/get/list")
	@ResponseBody
    public List<IEntity> getWorkingConditionsList() {
        return workingConditionsService.getWorkingConditionsList();
    }

    /**
     * カテゴリー別の従業員情報を取得する
     * @return
     */
    @PostMapping("/working_conditions/get/list/category")
	@ResponseBody
    public List<IEntity> getWorkingConditionsListByCategory(@RequestParam int category) {
        return workingConditionsService.getWorkingConditionsListByCategory(category);
    }

    /**
     * IDリストの従業員情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/working_conditions/save")
	@ResponseBody
    public IEntity saveWorkingConditions(@RequestBody WorkingConditionsEntity entity) {
        return workingConditionsService.saveWorkingConditions(entity);
    }

    /**
     * IDリストの従業員情報を削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/working_conditions/delete")
	@ResponseBody
    public IEntity deleteWorkingConditionsByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
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
    public String downloadCsvWorkingConditionsByIds(@RequestBody List<SimpleData> ids) {
        return workingConditionsService.downloadCsvWorkingConditionsByIds(ids);
    }
}
