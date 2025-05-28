package com.kyouseipro.neo.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.StaffEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.WorkingConditionsEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.cient.CompanyService;
import com.kyouseipro.neo.service.cient.OfficeService;
import com.kyouseipro.neo.service.cient.StaffService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.WorkingConditionsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TableEntityController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final OfficeService officeService;
    private final StaffService staffService;
    private final WorkingConditionsService workingConditionsService;
    private final ComboBoxService comboBoxService;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/{type}/get/id")
	@ResponseBody
    public IEntity getEntityById(@RequestParam int id, @PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.getEmployeeById(id);
            case "company":
                return companyService.getCompanyById(id);
            case "office":
                return officeService.getOfficeById(id);
            case "working_conditions":
                return workingConditionsService.getWorkingConditionsById(id);
            default:
                return null;
        }
    }

    /**
     * EntityListを取得する
     * @return
     */
    @GetMapping("/{type}/get/list")
	@ResponseBody
    public List<IEntity> getEntityList(@PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.getEmployeeList();
            case "company":
                return companyService.getCompanyList();
            case "office":
                return officeService.getOfficeList();
            case "staff":
                return staffService.getStaffList();
            case "working_conditions":
                return workingConditionsService.getWorkingConditionsList();
            default:
                return null;
        }
    }

    /**
     * カテゴリー別のEntityListを取得する
     * @return
     */
    @PostMapping("/{type}/get/list/category")
	@ResponseBody
    public List<IEntity> getEntityListByCategory(@RequestParam int category, @PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.getEmployeeListByCategory(category);
            case "company":
                return companyService.getCompanyListByCategory(category);
            case "office":
                return officeService.getOfficeListByCategory(category);
            case "working_conditions":
                return workingConditionsService.getWorkingConditionsListByCategory(category);
            default:
                return null;
        }
    }

    /**
     * IDリストの従業員情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/{type}/save")
	@ResponseBody
    public IEntity saveEntity(@RequestBody IEntity entity, @PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.saveEmployee((EmployeeEntity)entity);
            case "company":
                return companyService.saveCompany((CompanyEntity)entity);
            case "office":
                return officeService.saveOffice((OfficeEntity)entity);
            case "staff":
                return staffService.saveStaff((StaffEntity)entity);
            case "working_conditions":
                return workingConditionsService.saveWorkingConditions((WorkingConditionsEntity)entity);
            default:
                return null;
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/{type}/delete")
	@ResponseBody
    public IEntity deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
        String userName = principal.getAttribute("preferred_username");
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.deleteEmployeeByIds(ids, userName);
            case "company":
                return companyService.deleteCompanyByIds(ids, userName);
            case "office":
                return officeService.deleteOfficeByIds(ids, userName);
            case "staff":
                return staffService.deleteStaffByIds(ids, userName);
            case "working_conditions":
                return workingConditionsService.deleteWorkingConditionsByIds(ids, userName);
            default:
                return null;
        }
    }

    /**
     * IDリストからCSV用データを取得する
     * @param IDS
     * @return 
     */
    @PostMapping("/{type}/download/csv")
	@ResponseBody
    public String downloadCsvEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
        String userName = principal.getAttribute("preferred_username");
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.downloadCsvEmployeeByIds(ids, userName);
            case "company":
                return companyService.downloadCsvCompanyByIds(ids, userName);
            case "office":
                return officeService.downloadCsvOfficeByIds(ids, userName);
            case "staff":
                return staffService.downloadCsvStaffByIds(ids, userName);
            case "working_conditions":
                return workingConditionsService.downloadCsvWorkingConditionsByIds(ids, userName);
            default:
                return null;
        }
    }

    /**
     * すべてのコンボボックス用取引先情報を取得する
     * @return
     */
    @GetMapping("/{type}/get/combo")
	@ResponseBody
    public List<IEntity> getCompanyCombo(@PathVariable String type) {
        switch (type.toLowerCase()) {
            case "client":
                return comboBoxService.getClient();
            case "office":
                // return comboBoxService.getOffice();
            default:
                break;
        }
        return comboBoxService.getClient();
    }
}
