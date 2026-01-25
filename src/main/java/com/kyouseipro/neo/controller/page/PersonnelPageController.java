package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeListEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsEntity;
import com.kyouseipro.neo.entity.personnel.WorkingConditionsListEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.dto.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeListService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsListService;

import jakarta.servlet.http.HttpServletRequest;
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
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getEmployee(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "従業員");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/employee :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/employee.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期化されたエンティティ
        mv.addObject("formEntity", new EmployeeEntity());

        // 初期表示用従業員リスト取得
        List<EmployeeListEntity> origin = employeeListService.getList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getOwnCompanyList();
        mv.addObject("companyComboList", companyComboList);
        // List<SimpleData> clientComboList = comboBoxService.getClientList();
        // mv.addObject("clientComboList", clientComboList);
        List<OfficeListEntity> officeList = comboBoxService.getOfficeList();
        mv.addObject("officeList", officeList);
        List<SimpleData> employeeCategoryComboList = comboBoxService.getEmployeeCategory();
        mv.addObject("employeeCategoryComboList", employeeCategoryComboList);
        List<SimpleData> genderComboList = comboBoxService.getGender();
        mv.addObject("genderComboList", genderComboList);
        List<SimpleData> bloodTypeComboList = comboBoxService.getBloodType();
        mv.addObject("bloodTypeComboList", bloodTypeComboList);
        List<SimpleData> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<SimpleData> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());

        // EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "employee", "閲覧", 0, "");
		
        return mv;
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
    public ModelAndView timeworks(ModelAndView mv, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("contents/personnel/timeworks :: content"); // ★ テンプレート
        return mv;
    }
    // public ModelAndView showList(
    //         HttpServletRequest request,
    //         HttpSession session,
    //         HttpServletResponse response
    // ) throws IOException {

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) {
    //         return new ModelAndView("redirect:/login");
    //     }

    //     ModelAndView mv = new ModelAndView();
    //     mv.addObject("insertCss", "/css/personnel/timeworks.css");
    //     mv.addObject("title", "勤怠管理");
    //     mv.addObject("username", user.getAccount());

    //     historyService.save(user.getAccount(), "timeworks", "閲覧", 200, "");

    //     boolean isAjax =
    //         "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

    //     if (isAjax) {
    //         // body だけ返す
    //         mv.setViewName("contents/personnel/timeworks :: bodyFragment");
    //     } else {
    //         // layout ごと返す
    //         mv.setViewName("layouts/main");
    //         mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
    //         mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");
    //         mv.addObject("bodyFragmentName", "contents/personnel/timeworks :: bodyFragment");
    //     }

    //     return mv;
    // }

    /**
	 * 勤務条件
	 * @param mv
	 * @return
	 */
	@GetMapping("/working_conditions")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getWorkingConditions(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "勤務条件");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/working_conditions :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/working_conditions.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期化されたエンティティ
        mv.addObject("formEntity", new WorkingConditionsEntity());

        // 初期表示用従業員リスト取得
        List<WorkingConditionsListEntity> origin = workingConditionsListService.getList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<SimpleData> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getCode());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getCode());

        // EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "working_conditions", "閲覧", 200, "");
		
        return mv;
    }
}
