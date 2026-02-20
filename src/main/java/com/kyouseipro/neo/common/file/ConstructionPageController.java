package com.kyouseipro.neo.common.file;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConstructionPageController extends BaseController {
    /**
	 * 取引先
	 * @param mv
	 * @return
	 */
	@GetMapping("/construction")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getConstruction(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

        model.addAttribute("title", "施工管理");
		model.addAttribute("activeMenu", "management");
        model.addAttribute("activeSidebar", "construction");
        model.addAttribute("insertCss", "/css/construction/construction.css");

        return "contents/construction/construction";
    }
}
