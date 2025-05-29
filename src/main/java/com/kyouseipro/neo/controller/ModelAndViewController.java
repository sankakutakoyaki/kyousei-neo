package com.kyouseipro.neo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.common.QualificationsEntity;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.TimeworksListEntity;
import com.kyouseipro.neo.entity.person.WorkingConditionsEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.cient.CompanyService;
import com.kyouseipro.neo.service.cient.OfficeService;
import com.kyouseipro.neo.service.cient.StaffService;
import com.kyouseipro.neo.service.common.QualificationsService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ModelAndViewController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final OfficeService officeService;
    private final StaffService staffService;
    private final WorkingConditionsService workingConditionsService;
    private final QualificationsService qualificationsService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;
    
    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/employee")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getEmployee(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "従業員");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/employee :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/employee.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new EmployeeEntity());

        // 初期表示用従業員リスト取得
        List<IEntity> origin = employeeService.getEmployeeList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<IEntity> companyComboList = comboBoxService.getCompany();
        mv.addObject("companyComboList", companyComboList);
        List<IEntity> officeList = comboBoxService.getOffice();
        mv.addObject("officeList", officeList);
        List<IEntity> employeeCategoryComboList = comboBoxService.getEmployeeCategory();
        mv.addObject("employeeCategoryComboList", employeeCategoryComboList);
        List<IEntity> genderComboList = comboBoxService.getGender();
        mv.addObject("genderComboList", genderComboList);
        List<IEntity> bloodTypeComboList = comboBoxService.getBloodType();
        mv.addObject("bloodTypeComboList", bloodTypeComboList);
        List<IEntity> paymentMethodComboList = comboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<IEntity> payTypeComboList = comboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getNum());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getNum());
        mv.addObject("categoryConstructCode", Enums.employeeCategory.CONSTRUCT.getNum());

        // 履歴保存
        databaseService.saveHistory(userName, "employee", "閲覧", 200, "");
		
        return mv;
    }

    /**
	 * 取引先
	 * @param mv
	 * @return
	 */
	@GetMapping("/client")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getClient(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "取引先");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/sales/client :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/client.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("companyEntity", new CompanyEntity());
        mv.addObject("officeEntity", new OfficeEntity());
        mv.addObject("staffEntity", new StaffEntity());

        // 初期表示用Clientリスト取得
        List<IEntity> companyOrigin = companyService.getClientList();
        mv.addObject("companyOrigin", companyOrigin);
        // 初期表示用Officeリスト取得
        List<IEntity> officeOrigin = officeService.getOfficeList();
        mv.addObject("officeOrigin", officeOrigin);
        // 初期表示用Staffリスト取得
        List<IEntity> staffOrigin = staffService.getStaffList();
        mv.addObject("staffOrigin", staffOrigin);

        // コンボボックスアイテム取得
        List<IEntity> companyComboList = comboBoxService.getClient();
        mv.addObject("companyComboList", companyComboList);
        List<IEntity> officeComboList = comboBoxService.getOffice();
        mv.addObject("officeComboList", officeComboList);

        // 保存用コード
        mv.addObject("categoryPartnerCode", Enums.clientCategory.PARTNER.getNum());
        mv.addObject("categoryShipperCode", Enums.clientCategory.SHIPPER.getNum());
        mv.addObject("categorySupplierCode", Enums.clientCategory.SUPPLIER.getNum());
        mv.addObject("categoryServiceCode", Enums.clientCategory.SERVICE.getNum());

        // 履歴保存
        databaseService.saveHistory(userName, "companies", "閲覧", 200, "");
		
        return mv;
    }

    /**
	 * 資格
	 * @param mv
	 * @return
	 */
	@GetMapping("/qualifications/{type}")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getQualifications(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {

		mv.setViewName("layouts/main");
        mv.addObject("title", "資格");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
        mv.addObject("bodyFragmentName", "contents/common/qualifications :: bodyFragment");
        mv.addObject("insertCss", "/css/common/qualifications.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new QualificationsEntity());

        switch (type.toLowerCase()) {
            case "employee":
                mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
                // 初期表示用資格情報リスト取得
                List<IEntity> qualificationsOrigin = qualificationsService.getEmployeeQualificationsList();
                mv.addObject("origin", qualificationsOrigin);
                // コンボボックスアイテム取得
                List<IEntity> qualificationComboList = comboBoxService.getQualificationMaster();
                mv.addObject("qualificationComboList", qualificationComboList);
                break;
            case "company":
                mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
                // 初期表示用資格情報リスト取得
                List<IEntity> licensesOrigin = qualificationsService.getCompanyQualificationsList();
                mv.addObject("origin", licensesOrigin);
                // コンボボックスアイテム取得
                List<IEntity> licenseComboList = comboBoxService.getLicenseMaster();
                mv.addObject("qualificationComboList", licenseComboList);
                break;
            default:
                break;
        }

        // 履歴保存
        databaseService.saveHistory(userName, "qualifications", "閲覧", 200, "");
		
        return mv;
    }

    /**
	 * 勤務条件
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
     * 打刻一覧画面を呼び出す
     * @param mv
     * @param token
     * @return ModelAndView
     */
	@GetMapping("/timeworks")
	public ModelAndView showList(ModelAndView mv, OAuth2AuthenticationToken token, @AuthenticationPrincipal OidcUser principal) {
        mv.setViewName("layouts/main");
        mv.addObject("title", "勤怠");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/timeworks :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/timeworks.css");

        // ユーザー名
        String userName = principal.getAttribute("preferred_username");
        mv.addObject("username", userName);
        // MVを設定
		
        // 勤怠データ新規・更新用
        TimeworksListEntity entity = new TimeworksListEntity();
        mv.addObject("entity", entity);

        // 履歴保存
        databaseService.saveHistory(userName, "timeworks", "閲覧", 200, "");
	    return mv;
	}
}
