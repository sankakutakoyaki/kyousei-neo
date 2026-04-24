package com.kyouseipro.neo.pages;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.abstracts.BaseController;
import com.kyouseipro.neo.common.enums.code.BloodType;
import com.kyouseipro.neo.common.enums.code.ClientCategory;
import com.kyouseipro.neo.common.enums.code.CompanyCategory;
import com.kyouseipro.neo.common.enums.code.EmployeeCategory;
import com.kyouseipro.neo.common.enums.code.Gender;
import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import com.kyouseipro.neo.domain.corporation.company.CompanyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CorporationPageController extends BaseController {
    private final CompanyService companyService;

    /**
	 * 取引先
	 * @param mv
	 * @return
	 */
	@GetMapping("/client")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getClient() {
        return "fragments/pages/corporation/client/content :: content";
    }

    /**
	 * パートナー
	 * @param mv
	 * @return
	 */
	@GetMapping("/partner")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getPartner() {
        return "fragments/pages/corporation/partner/content :: content";
    }

    @GetMapping("/api/client/init/cache")
    @ResponseBody
    public Map<String, Object> initClient() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class),
                "companyCategory", EnumUtil.toMap(CompanyCategory.class)
            ),
            "page", Map.of(
                "companyComboList", companyService.findComboClientAll(),
                "officeComboList", officeService.findComboClientAll(),
                "clientCategoryComboList", EnumUtil.toCombo(ClientCategory.class)
            )
        );
    }

    @GetMapping("/api/partner/init/cache")
    @ResponseBody
    public Map<String, Object> initPartner() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class),
                "companyCategory", EnumUtil.toMap(CompanyCategory.class),
                "employeeCategory", EnumUtil.toMap(EmployeeCategory.class)
            ),
            "page", Map.of(
                "companyComboList", companyService.findComboByCategory(CompanyCategory.PARTNER.getCode()),
                "genderComboList", EnumUtil.toCombo(Gender.class),
                "bloodTypeComboList", EnumUtil.toCombo(BloodType.class)
            )
        );
    }
}

// @GetMapping("/client")
// @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
// public String getClient(Model model, HttpSession session, HttpServletResponse response) throws IOException {
//     // EmployeeEntity user = getLoginUser(session, response);
//     // if (user == null) return null; // リダイレクト

//     // model.addAttribute("title", "取引先");
//     // model.addAttribute("activeMenu", "regist");
//     // model.addAttribute("activeSidebar", "client");
//     // model.addAttribute("insertCss", "/css/corporation/client.css");

//     // // 初期化されたエンティティ
//     // model.addAttribute("companyEntity", new CompanyEntity());
//     // model.addAttribute("officeEntity", new OfficeEntity());
//     // model.addAttribute("staffEntity", new StaffEntity());

//     // // 初期表示用Clientリスト取得
//     // List<CompanyListResponse> companyOrigin = companyListService.getClientList();
//     // model.addAttribute("companyOrigin", companyOrigin);

//     // // 保存用コード
//     // Map<String, Integer> categoryCodes = Arrays.stream(Enums.clientCategory.values())
//     //     .collect(Collectors.toMap(
//     //         Enums.clientCategory::name,
//     //         Enums.clientCategory::getCode
//     //     ));
//     // model.addAttribute("categoryCodes", categoryCodes);
    
//     // return "contents/corporation/client";
//     return "fragments/pages/corporation/client/content :: content";
// }

// /**
//  * パートナー
//  * @param mv
//  * @return
//  */
// @GetMapping("/partner")
// @PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
// public String getPartner(Model model, HttpSession session, HttpServletResponse response) throws IOException {
//     // EmployeeEntity user = getLoginUser(session, response);
//     // if (user == null) return null; // リダイレクト

//     // model.addAttribute("title", "パートナー");
//     // // model.addAttribute("sidebarFragmentName", "~{fragments/common/menu :: registFragment}");
//     // // model.addAttribute("activeMenu", "regist");
//     // // model.addAttribute("activeSidebar", "partner");
//     // model.addAttribute("insertCss", "/css/pages/corporation/partner.css");

//     // // // 初期化されたエンティティ
//     // // model.addAttribute("companyEntity", new CompanyEntity());
//     // // model.addAttribute("staffEntity", new EmployeeEntity());

//     // // // 初期表示用リスト取得
//     // // List<CompanyListResponse> companyOrigin = companyListService.getPartnerList();
//     // // model.addAttribute("companyOrigin", companyOrigin);
//     // // List<EmployeeListEntity> employeeOrigin = employeeListService.getListByCategoryId(Enums.employeeCategory.CONSTRUCT.getCode());
//     // // model.addAttribute("employeeOrigin", employeeOrigin);

//     // // コンボボックスアイテム取得
//     // // List<SimpleData> companyComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.PARTNER.getCode());
//     // // model.addAttribute("companyComboList", companyComboList);
//     // // List<SimpleData> genderComboList = comboBoxService.getGender();
//     // // model.addAttribute("genderComboList", genderComboList);
//     // // List<SimpleData> bloodTypeComboList = comboBoxService.getBloodType();
//     // // model.addAttribute("bloodTypeComboList", bloodTypeComboList);

//     // // model.addAttribute("state", Enums.state.getMap());
//     // // model.addAttribute("clientCategory", Enums.clientCategory.getMap());
//     // // model.addAttribute("employeeCategory", Enums.employeeCategory.getMap());


//     // // int code = codeMasterService.getCode(CodeType.CLIENT_CATEGORY, "PARTNER");
    
//     // model.addAttribute("companyComboList", companyService.findComboByCategory(ClientCategory.PARTNER.getCode()));
//     // model.addAttribute("genderComboList", EnumUtil.toCombo(Gender.class));
//     // model.addAttribute("bloodTypeComboList", EnumUtil.toCombo(BloodType.class));
//     // // model.addAttribute("genderComboList", comboService.getCombo(CodeType.GENDER));
//     // // model.addAttribute("bloodTypeComboList", comboService.getCombo(CodeType.BLOOD_TYPE));

//     // // model.addAttribute("state", EnumUtil.toCombo(State.class));        
//     // // model.addAttribute("clientCategory", EnumUtil.toCombo(EmployeeCategory.class));
//     // // model.addAttribute("employeeCategory", EnumUtil.toCombo(ClientCategory.class));
//     // model.addAttribute("state", EnumUtil.toMap(State.class));
//     // model.addAttribute("clientCategory", EnumUtil.toMap(ClientCategory.class));
//     // model.addAttribute("employeeCategory", EnumUtil.toMap(EmployeeCategory.class));

//     return "fragments/pages/corporation/partner/content :: content";
// }