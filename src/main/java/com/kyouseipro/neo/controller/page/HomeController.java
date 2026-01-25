package com.kyouseipro.neo.controller.page;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.dto.HistoryService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController extends BaseController {
    private final HistoryService historyService;

	/**
	 * 営業
	 * @param mv
	 * @return
	 */
	@GetMapping("/list")
	// @ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getList(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "一覧");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: listFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/list :: bodyFragment");
    //     mv.addObject("insertCss", "/css/index/list.css");

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) return null; // リダイレクト済みなので処理は止まる
	// 	// ユーザー名
	// 	// String userName = principal.getAttribute("preferred_username");
	// 	// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
	// 	// mv.addObject("entity", entity);
	// 	// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

	// 	// EmployeeEntity user = getLoginUser(session);
	// 	historyService.save(user.getAccount(), "list", "閲覧", 200, "");
		
    //     return mv;
    // }
	public String getList(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "list", "閲覧", 200, "");

		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: listFragment}");
		model.addAttribute("activeMenu", "list");

		return "contents/index/list";
	}
	
	/**
	 * 営業
	 * @param mv
	 * @return
	 */
	@GetMapping("/sales")
	// @ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getSales(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "営業");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: salesFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/sales :: bodyFragment");
    //     mv.addObject("insertCss", "/css/index/sales.css");

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) return null; // リダイレクト済みなので処理は止まる
	// 	// ユーザー名
	// 	// String userName = principal.getAttribute("preferred_username");
	// 	// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
	// 	// mv.addObject("entity", entity);
	// 	// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

	// 	// EmployeeEntity user = getLoginUser(session);
	// 	historyService.save(user.getAccount(), "sales", "閲覧", 200, "");
		
    //     return mv;
    // }
	public String getSales(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "sales", "閲覧", 200, "");

		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: salesFragment}");
		model.addAttribute("activeMenu", "sales");

		return "contents/index/sales";
	}

	/**
	 * 人事
	 * @param mv
	 * @return
	 */
	@GetMapping("/personnel")
	// @ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getPersonnel(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "人事");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/personnel :: bodyFragment");
    //     mv.addObject("insertCss", "/css/index/personnel.css");

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) return null; // リダイレクト済みなので処理は止まる
	// 	// ユーザー名
	// 	// String userName = principal.getAttribute("preferred_username");
	// 	// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
	// 	// mv.addObject("entity", entity);
	// 	// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

	// 	// EmployeeEntity user = getLoginUser(session);
	// 	historyService.save(user.getAccount(), "personnel", "閲覧", 200, "");
		
    //     return mv;
    // }
// 	public ModelAndView getPersonnel(ModelAndView mv, HttpSession session, HttpServletResponse response) throws IOException {
// System.out.println("test");
// 		EmployeeEntity user = getLoginUser(session, response);
// 		if (user == null) return null; // リダイレクト済み

// 		historyService.save(user.getAccount(), "personnel", "閲覧", 200, "");

// 		mv.setViewName("contents/index/personnel"); // ★ テンプレート
// 		mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");

// 		return mv;
// 	}
	public String getPersonnel(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "personnel", "閲覧", 200, "");

		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: personnelFragment}");
		model.addAttribute("activeMenu", "personnel");

		return "contents/index/personnel";
	}
	/**
	 * 管理
	 * @param mv
	 * @return
	 */
	@GetMapping("/management")
	// @ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getManagement(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "管理");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: managementFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/management :: bodyFragment");
    //     mv.addObject("insertCss", "/css/index/management.css");

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) return null; // リダイレクト済みなので処理は止まる
	// 	// ユーザー名
	// 	// String userName = principal.getAttribute("preferred_username");
	// 	// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
	// 	// mv.addObject("entity", entity);
	// 	// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

	// 	// EmployeeEntity user = getLoginUser(session);
	// 	historyService.save(user.getAccount(), "management", "閲覧", 200, "");
		
    //     return mv;
    // }
	public String getManagement(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "management", "閲覧", 200, "");

		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: managementFragment}");
		model.addAttribute("activeMenu", "management");

		return "contents/index/management";
	}

	/**
	 * 登録
	 * @param mv
	 * @return
	 */
	// @GetMapping("/regist")
	// @ResponseBody
	// @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getRegistration(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "登録");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/regist :: bodyFragment");
    //     mv.addObject("insertCss", "/css/index/regist.css");
	@GetMapping("/regist")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getRegistration(
	// 		ModelAndView mv,
	// 		HttpSession session,
	// 		HttpServletResponse response
	// ) throws IOException {

	// 	EmployeeEntity user = getLoginUser(session, response);
	// 	if (user == null) return null; // リダイレクト済み

	// 	historyService.save(user.getAccount(), "regist", "閲覧", 200, "");

	// 	mv.setViewName("contents/index/regist :: content"); // ★ テンプレート
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");

	// 	return mv;
	// }
	public String getRegistration(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "regist", "閲覧", 200, "");

		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: registFragment}");
		model.addAttribute("activeMenu", "regist");

		return "contents/index/regist";
	}

	/**
	 * リサイクル
	 * @param mv
	 * @return
	 */
	@GetMapping("/recycle")
	// @ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	// public ModelAndView getRecycle(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
	// 	mv.setViewName("layouts/main");
	// 	mv.addObject("title", "リサイクル");
    //     mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
	// 	mv.addObject("sidebarFragmentName", "fragments/common/menu :: recycleFragment");
    //     mv.addObject("bodyFragmentName", "contents/index/recycle :: bodyFragment");
    //     mv.addObject("insertCss", "/css/index/recycle.css");

    //     EmployeeEntity user = getLoginUser(session, response);
    //     if (user == null) return null; // リダイレクト済みなので処理は止まる
	// 	// ユーザー名
	// 	// String userName = principal.getAttribute("preferred_username");
	// 	// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
	// 	// mv.addObject("entity", entity);
	// 	// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

	// 	// EmployeeEntity user = getLoginUser(session);
	// 	historyService.save(user.getAccount(), "recycle", "閲覧", 200, "");
		
    //     return mv;
    // }
	public String getRecycle(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "recycle", "閲覧", 200, "");

		model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: recycleFragment}");
		model.addAttribute("activeMenu", "recycle");

		return "contents/index/recycle";
	}
}
