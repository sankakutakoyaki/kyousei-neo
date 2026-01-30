package com.kyouseipro.neo.index;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.history.service.HistoryService;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

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
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getList(Model model, HttpSession session, HttpServletResponse response) throws IOException {
		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "list", "閲覧", 200, "");

		model.addAttribute("title", "一覧");
		model.addAttribute("activeMenu", "list");
        model.addAttribute("insertCss", "/css/index/list.css");

		return "contents/index/list";
	}
	
	/**
	 * 営業
	 * @param mv
	 * @return
	 */
	@GetMapping("/sales")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getSales(Model model, HttpSession session, HttpServletResponse response) throws IOException {
		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "sales", "閲覧", 200, "");

		model.addAttribute("title", "営業");
		model.addAttribute("activeMenu", "sales");
        model.addAttribute("insertCss", "/css/index/sales.css");

		return "contents/index/sales";
	}

	/**
	 * 人事
	 * @param mv
	 * @return
	 */
	@GetMapping("/personnel")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getPersonnel(Model model, HttpSession session, HttpServletResponse response) throws IOException {

		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "personnel", "閲覧", 200, "");

		model.addAttribute("title", "人事");
		model.addAttribute("activeMenu", "personnel");
        model.addAttribute("insertCss", "/css/index/personnel.css");

		return "contents/index/personnel";
	}
	/**
	 * 管理
	 * @param mv
	 * @return
	 */
	@GetMapping("/management")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getManagement(Model model, HttpSession session, HttpServletResponse response) throws IOException {
		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "management", "閲覧", 200, "");

		model.addAttribute("title", "管理");
		model.addAttribute("activeMenu", "management");
        model.addAttribute("insertCss", "/css/index/management.css");

		return "contents/index/management";
	}

	/**
	 * 登録
	 * @param mv
	 * @return
	 */
	@GetMapping("/regist")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getRegistration(Model model, HttpSession session, HttpServletResponse response) throws IOException {
		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "regist", "閲覧", 200, "");

		model.addAttribute("title", "登録");
		model.addAttribute("activeMenu", "regist");
        model.addAttribute("insertCss", "/css/index/regist.css");

		return "contents/index/regist";
	}

	/**
	 * リサイクル
	 * @param mv
	 * @return
	 */
	@GetMapping("/recycle")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public String getRecycle(Model model, HttpSession session, HttpServletResponse response) throws IOException {
		EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト済み

		historyService.save(user.getAccount(), "recycle", "閲覧", 200, "");

		model.addAttribute("title", "リサイクル");
		model.addAttribute("activeMenu", "recycle");
        model.addAttribute("insertCss", "/css/index/recycle.css");

		return "contents/index/recycle";
	}
}
