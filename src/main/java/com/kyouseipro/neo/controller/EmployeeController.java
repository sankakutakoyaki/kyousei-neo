package com.kyouseipro.neo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

@Controller
public class EmployeeController {
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
		EmployeeEntity user = (EmployeeEntity) EmployeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new EmployeeEntity());

        // 初期表示用従業員リスト取得
        List<IEntity> origin = EmployeeService.getEmployeeList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<IEntity> companyComboList = ComboBoxService.getCompany();
        mv.addObject("companyComboList", companyComboList);
        List<IEntity> officeList = ComboBoxService.getOffice();
        mv.addObject("officeList", officeList);
        List<IEntity> employeeCategoryComboList = ComboBoxService.getEmployeeCategory();
        mv.addObject("employeeCategoryComboList", employeeCategoryComboList);
        List<IEntity> genderComboList = ComboBoxService.getGender();
        mv.addObject("genderComboList", genderComboList);
        List<IEntity> bloodTypeComboList = ComboBoxService.getBloodType();
        mv.addObject("bloodTypeComboList", bloodTypeComboList);
        List<IEntity> paymentMethodComboList = ComboBoxService.getPaymentMethod();
        mv.addObject("paymentMethodComboList", paymentMethodComboList);
        List<IEntity> payTypeComboList = ComboBoxService.getPayType();
        mv.addObject("payTypeComboList", payTypeComboList);

        // 保存用コード
        mv.addObject("categoryEmployeeCode", Enums.employeeCategory.FULLTIME.getNum());
        mv.addObject("categoryParttimeCode", Enums.employeeCategory.PARTTIME.getNum());
        mv.addObject("categoryConstructCode", Enums.employeeCategory.CONSTRUCT.getNum());

        // 履歴保存
        DatabaseService.saveHistory(userName, "employee", "閲覧", 200, "");
		
        return mv;
    }

    /**
     * IDから従業員情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/employee/get/id")
	@ResponseBody
    public IEntity getEmployeeById(@RequestParam int id) {
        return EmployeeService.getEmployeeById(id);
    }

    /**
     * すべての従業員情報を取得する
     * @return
     */
    @GetMapping("/employee/get/list")
	@ResponseBody
    public List<IEntity> getEmployeeList() {
        return EmployeeService.getEmployeeList();
    }

    /**
     * カテゴリー別の従業員情報を取得する
     * @return
     */
    @PostMapping("/employee/get/list/category")
	@ResponseBody
    public List<IEntity> getEmployeeListByCategory(@RequestParam int category) {
        return EmployeeService.getEmployeeListByCategory(category);
    }

    /**
     * IDリストの従業員情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/employee/save")
	@ResponseBody
    public IEntity saveEmployee(@RequestBody EmployeeEntity entity) {
        return EmployeeService.saveEmployee(entity);
    }

    /**
     * IDリストの従業員情報を削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/employee/delete")
	@ResponseBody
    public IEntity deleteEmployeeByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return EmployeeService.deleteEmployeeByIds(ids, userName);
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/employee/download/csv")
	@ResponseBody
    public String downloadCsvEmployeeByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        return EmployeeService.downloadCsvEmployeeByIds(ids);
    }
}
