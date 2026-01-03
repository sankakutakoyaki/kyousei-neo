package com.kyouseipro.neo.controller.page;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.document.HistoryService;

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
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getList(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "一覧");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: listFragment");
        mv.addObject("bodyFragmentName", "contents/index/list :: bodyFragment");
        mv.addObject("insertCss", "/css/index/list.css");
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
		// mv.addObject("entity", entity);
		// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

		EmployeeEntity user = getLoginUser(session);
		historyService.save(user.getAccount(), "list", "閲覧", 200, "");
		
        return mv;
    }	
	
	/**
	 * 営業
	 * @param mv
	 * @return
	 */
	@GetMapping("/sales")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getSales(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "営業");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/index/sales :: bodyFragment");
        mv.addObject("insertCss", "/css/index/sales.css");
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
		// mv.addObject("entity", entity);
		// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

		EmployeeEntity user = getLoginUser(session);
		historyService.save(user.getAccount(), "sales", "閲覧", 200, "");
		
        return mv;
    }

	/**
	 * 人事
	 * @param mv
	 * @return
	 */
	@GetMapping("/personnel")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getPersonnel(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "人事");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/index/personnel :: bodyFragment");
        mv.addObject("insertCss", "/css/index/personnel.css");
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
		// mv.addObject("entity", entity);
		// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

		EmployeeEntity user = getLoginUser(session);
		historyService.save(user.getAccount(), "personnel", "閲覧", 200, "");
		
        return mv;
    }

	/**
	 * 管理
	 * @param mv
	 * @return
	 */
	@GetMapping("/management")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getManagement(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "管理");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: managementFragment");
        mv.addObject("bodyFragmentName", "contents/index/management :: bodyFragment");
        mv.addObject("insertCss", "/css/index/management.css");
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
		// mv.addObject("entity", entity);
		// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

		EmployeeEntity user = getLoginUser(session);
		historyService.save(user.getAccount(), "management", "閲覧", 200, "");
		
        return mv;
    }

	/**
	 * 登録
	 * @param mv
	 * @return
	 */
	@GetMapping("/regist")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getRegistration(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "登録");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: registFragment");
        mv.addObject("bodyFragmentName", "contents/index/regist :: bodyFragment");
        mv.addObject("insertCss", "/css/index/regist.css");
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
		// mv.addObject("entity", entity);
		// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

		EmployeeEntity user = getLoginUser(session);
		historyService.save(user.getAccount(), "management", "閲覧", 200, "");
		
        return mv;
    }

	/**
	 * リサイクル
	 * @param mv
	 * @return
	 */
	@GetMapping("/recycle")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getRecycle(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session) {
		mv.setViewName("layouts/main");
		mv.addObject("title", "リサイクル");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: recycleFragment");
        mv.addObject("bodyFragmentName", "contents/index/recycle :: bodyFragment");
        mv.addObject("insertCss", "/css/index/recycle.css");
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity entity = (EmployeeEntity) employeeService.getByAccount(userName);
		// mv.addObject("entity", entity);
		// mv.addObject("entity", employeeService.getByAccount(userName).orElse(null));

		EmployeeEntity user = getLoginUser(session);
		historyService.save(user.getAccount(), "recycle", "閲覧", 200, "");
		
        return mv;
    }
}
