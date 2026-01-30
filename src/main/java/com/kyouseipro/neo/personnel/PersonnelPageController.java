package com.kyouseipro.neo.personnel;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Utilities;
import com.kyouseipro.neo.common.history.service.HistoryService;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeListEntity;
import com.kyouseipro.neo.personnel.employee.service.EmployeeListService;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsEntity;
import com.kyouseipro.neo.personnel.workingconditions.entity.WorkingConditionsListEntity;
import com.kyouseipro.neo.personnel.workingconditions.service.WorkingConditionsListService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PersonnelPageController extends BaseController {
    private final EmployeeListService employeeListService;
    private final WorkingConditionsListService workingConditionsListService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/employee")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getEmployee(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "emplloyees", "閲覧", 200, "");

        model.addAttribute("title", "従業員");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("activeSidebar", "employee");
        model.addAttribute("insertCss", "/css/personnel/employee.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new EmployeeEntity());

        // 初期表示用従業員リスト取得
        List<EmployeeListEntity> origin = employeeListService.getList();
        model.addAttribute("origin", origin);

        List<SimpleData> officeComboList = comboBoxService.getSimpleOwnOfficeList();
        model.addAttribute("officeComboList", officeComboList);
        List<SimpleData> genderComboList = comboBoxService.getGender();
        model.addAttribute("genderComboList", genderComboList);
        List<SimpleData> bloodTypeComboList = comboBoxService.getBloodType();
        model.addAttribute("bloodTypeComboList", bloodTypeComboList);

        // 保存用コード
        model.addAttribute("ownCompanyId", Utilities.getOwnCompanyId());
        model.addAttribute("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        model.addAttribute("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());

        return "contents/personnel/employee";
    }

    /**
     * 打刻一覧画面を呼び出す
     * @param mv
     * @param token
     * @return ModelAndView
     */
	@GetMapping("/timeworks")
    @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView showList(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
    //     // mv.setViewName("layouts/main");
    //     // mv.addObject("title", "勤怠");
    //     // mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	// mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");
    //     // mv.addObject("bodyFragmentName", "contents/personnel/timeworks :: bodyFragment");
    //     mv.addObject("insertCss", "/css/personnel/timeworks.css");

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) return null; // リダイレクト済みなので処理は止まる

    //     // // 勤怠データ新規・更新用
    //     // TimeworksListEntity entity = new TimeworksListEntity();
    //     // mv.addObject("entity", entity);
    //     // // 初期表示用営業所ID
    //     // mv.addObject("officeId", session.getAttribute("officeId") == null ? "1000" : session.getAttribute("officeId").toString());
    //     // // 営業所リスト
    //     // List<SimpleData> officeList = comboBoxService.getSimpleOfficeList();
    //     // mv.addObject("officeList", officeList);
    //     // // 初期表示用従業員リスト取得
    //     // List<EmployeeListEntity> employeeList = employeeListService.getList();
    //     // mv.addObject("employeeList", employeeList);
    //     // // 完了コードを取得
    //     // mv.addObject("completeNum", Enums.state.COMPLETE.getCode());

    //     // EmployeeEntity user = getLoginUser(session);
    //     mv.addObject("username", user.getAccount());
    //     historyService.save(user.getAccount(), "timeworks", "閲覧", 200, "");
	//     return mv;
    // }
    public String getTimeworks(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "timeworks", "閲覧", 200, "");

        model.addAttribute("title", "勤怠");
		model.addAttribute("activeMenu", "personnel");
        model.addAttribute("activeSidebar", "timeworks");
        model.addAttribute("insertCss", "/css/personnel/timeworks.css");
        
        return "contents/personnel/timeworks";
    }

    /**
	 * 勤務条件
	 * @param mv
	 * @return
	 */
	@GetMapping("/working_conditions")
	// @ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
    public String getWorkingConditions(Model model, HttpSession session, HttpServletResponse response) throws IOException {

        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "working_conditions", "閲覧", 200, "");

        model.addAttribute("title", "勤務条件");
		model.addAttribute("activeMenu", "personnel");
        model.addAttribute("activeSidebar", "working-conditions");
        model.addAttribute("insertCss", "/css/personnel/working-conditions.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new WorkingConditionsEntity());

        // 初期表示用従業員リスト取得
        List<WorkingConditionsListEntity> origin = workingConditionsListService.getList();
        model.addAttribute("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> paymentMethodComboList = comboBoxService.getPaymentMethod();
        model.addAttribute("paymentMethodComboList", paymentMethodComboList);
        List<SimpleData> payTypeComboList = comboBoxService.getPayType();
        model.addAttribute("payTypeComboList", payTypeComboList);

        // 保存用コード
        model.addAttribute("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        model.addAttribute("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());

        return "contents/personnel/working_conditions";
    }
}
