package com.kyouseipro.neo.recycle;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.combo.entity.ComboData;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.corporation.office.entity.OfficeListEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.recycle.maker.entity.RecycleMakerEntity;
import com.kyouseipro.neo.recycle.maker.service.RecycleMakerService;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntity;
import com.kyouseipro.neo.recycle.regist.entity.RecycleEntityRequest;
import com.kyouseipro.neo.recycle.regist.service.RecycleService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecyclePageController extends BaseController {
    private final RecycleService recycleService;
    private final RecycleMakerService recycleMakerService;
    private final ComboBoxService comboBoxService;

	@GetMapping("/recycle/regist")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getRecycle(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

        model.addAttribute("title", "リサイクル");
		model.addAttribute("activeMenu", "recycle");
        model.addAttribute("activeSidebar", "regist");
        model.addAttribute("insertCss", "/css/recycle/recycle.css");

        // 初期化されたエンティティ
        // model.addAttribute("formEntity", new RecycleEntity());
        model.addAttribute("formEntity", new RecycleEntityRequest());

        // 初期表示用リスト取得
        List<RecycleEntity> origin = recycleService.getBetween(LocalDate.now(), LocalDate.now(), "regist");
        model.addAttribute("origin", origin);
        // コンボボックスアイテム取得（自社含む小売業者）
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorListAddTopOfOwnCompany();
        model.addAttribute("companyComboList", companyComboList);
        // コンボボックスアイテム取得（処理場）
        List<SimpleData> disposalSiteComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.FACILITY.getCode());
        model.addAttribute("disposalSiteComboList", disposalSiteComboList);
        // 支店リストを取得
        List<OfficeListEntity> officeComboList = comboBoxService.getOfficeList();
        model.addAttribute("officeComboList", officeComboList);
        // メーカーコンボボックスアイテム取得
        List<ComboData> makerComboList = comboBoxService.getRecycleMakerComboList();
        model.addAttribute("makerComboList", makerComboList);
        // 品目コンボボックスアイテム取得
        List<ComboData> itemComboList = comboBoxService.getRecycleItemComboList();
        model.addAttribute("itemComboList", itemComboList);
		
        return "contents/recycle/recycle";
    }

	@GetMapping("/recycle/maker")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getMaker(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        EmployeeEntity user = getLoginUser(session, response);
		if (user == null) return null; // リダイレクト

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
