package com.kyouseipro.neo.controller.page;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.recycle.RecycleEntity;
import com.kyouseipro.neo.entity.recycle.RecycleMakerEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.dto.HistoryService;
import com.kyouseipro.neo.service.recycle.RecycleMakerService;
import com.kyouseipro.neo.service.recycle.RecycleService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecyclePageController extends BaseController {
    private final RecycleService recycleService;
    private final RecycleMakerService recycleMakerService;
    private final HistoryService historyService;
    private final ComboBoxService comboBoxService;

	@GetMapping("/recycle/regist")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getRecycle(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "recycle", "閲覧", 200, "");

        model.addAttribute("title", "リサイクル");
		model.addAttribute("activeMenu", "recycle");
        model.addAttribute("activeSidebar", "regist");
        model.addAttribute("insertCss", "/css/recycle/recycle.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new RecycleEntity());

        // 初期表示用リスト取得
        List<RecycleEntity> origin = recycleService.getBetween(LocalDate.now(), LocalDate.now(), "regist");
        model.addAttribute("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorListAddTopOfOwnCompany();
        model.addAttribute("companyComboList", companyComboList);
        // 支店リストを取得
        List<OfficeListEntity> officeComboList = comboBoxService.getOfficeList();
        model.addAttribute("officeComboList", officeComboList);

        model.addAttribute("deleteCode", Enums.state.DELETE.getCode());
		
        return "contents/recycle/recycle";
    }

	@GetMapping("/recycle/maker")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getMaker(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

		historyService.save(user.getAccount(), "recycle_maker", "閲覧", 200, "");

        model.addAttribute("title", "リサイクル");
		model.addAttribute("activeMenu", "recycle");
        model.addAttribute("activeSidebar", "maker");
        model.addAttribute("insertCss", "/css/recycle/maker.css");

        // 初期化されたエンティティ
        model.addAttribute("formEntity", new RecycleMakerEntity());

        // 初期表示用リスト取得
        List<RecycleMakerEntity> origin = recycleMakerService.getList();
        model.addAttribute("origin", origin);
        // コンボボックスアイテム取得
        List<SimpleData> groupComboList = comboBoxService.getRecycleGroupList();
        model.addAttribute("groupComboList", groupComboList);

        model.addAttribute("otherCode", 3);
		
        return "contents/recycle/maker";
    }
}
