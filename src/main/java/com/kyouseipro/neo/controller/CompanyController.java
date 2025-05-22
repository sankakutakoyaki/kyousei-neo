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
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.cient.CompanyService;
import com.kyouseipro.neo.service.cient.OfficeService;
import com.kyouseipro.neo.service.personnel.EmployeeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final OfficeService officeService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;
    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/client")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getEmployee(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
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

        // 初期表示用Clientリスト取得
        List<IEntity> companyOrigin = companyService.getClientList();
        mv.addObject("companyOrigin", companyOrigin);
        // 初期表示用Officeリスト取得
        List<IEntity> officeOrigin = officeService.getOfficeList();
        mv.addObject("officeOrigin", officeOrigin);

        // コンボボックスアイテム取得
        List<IEntity> companyComboList = comboBoxService.getClient();
        mv.addObject("companyComboList", companyComboList);

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
     * IDから会社情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/company/get/id")
	@ResponseBody
    public IEntity getCompanyById(@RequestParam int id) {
        return companyService.getCompanyById(id);
    }

    /**
     * すべての取引先情報を取得する
     * @return
     */
    @GetMapping("/client/get/list")
	@ResponseBody
    public List<IEntity> getCompanyList() {
        return companyService.getCompanyList();
    }

    /**
     * すべてのコンボボックス用取引先情報を取得する
     * @return
     */
    @GetMapping("/client/get/combo")
	@ResponseBody
    public List<IEntity> getCompanyCombo() {
        return comboBoxService.getClient();
    }

    /**
     * カテゴリー別の会社情報を取得する
     * @return
     */
    @PostMapping("/company/get/list/category")
	@ResponseBody
    public List<IEntity> getCompanyListByCategory(@RequestParam int category) {
        return companyService.getCompanyListByCategory(category);
    }

    /**
     * 会社情報を保存する
     * @param 
     * @return 
     */
    @PostMapping("/company/save")
	@ResponseBody
    public IEntity saveCompany(@RequestBody CompanyEntity entity) {
        return companyService.saveCompany(entity);
    }

    /**
     * IDリストの会社情報を削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/company/delete")
	@ResponseBody
    public IEntity deleteCompanyByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return companyService.deleteCompanyByIds(ids, userName);
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/company/download/csv")
	@ResponseBody
    public String downloadCsvCompanyByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        return companyService.downloadCsvCompanyByIds(ids);
    }

    /**
     * IDから支店情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/company/office/get/id")
	@ResponseBody
    public IEntity getOfficeById(@RequestParam int id) {
        return officeService.getOfficeById(id);
    }

    /**
     * すべての取引先支店情報を取得する
     * @return
     */
    @GetMapping("/client/office/get/list")
	@ResponseBody
    public List<IEntity> getOfficeList() {
        return officeService.getOfficeList();
    }

    /**
     * カテゴリー別の支店情報を取得する
     * @return
     */
    @PostMapping("/office/get/list/category")
	@ResponseBody
    public List<IEntity> getOfficeListByCategory(@RequestParam int category) {
        return officeService.getOfficeListByCategory(category);
    }

    /**
     * 支店情報を保存する
     * @param 
     * @return 
     */
    @PostMapping("/office/save")
	@ResponseBody
    public IEntity saveOffice(@RequestBody OfficeEntity entity) {
        return officeService.saveOffice(entity);
    }

    /**
     * IDリストの支店情報を削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/office/delete")
	@ResponseBody
    public IEntity deleteOfficeByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return officeService.deleteOfficeByIds(ids, userName);
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/office/download/csv")
	@ResponseBody
    public String downloadCsvOfficeByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal) {
        return officeService.downloadCsvOfficeByIds(ids);
    }
}