package com.kyouseipro.neo.controller.api;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EmployeeApiController {
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final OfficeService officeService;
    private final StaffService staffService;
    private final WorkingConditionsService workingConditionsService;
    private final ComboBoxService comboBoxService;
    private final QualificationsService qualificationsService;
    private final TimeworksService timeworksService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * IDからEntityを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/{type}/get/id")
	@ResponseBody
    public Entity getEntityById(@RequestParam int id, @PathVariable String type) {
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
    public List<Entity> getEntityList(@PathVariable String type) {
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
    public List<Entity> getEntityListByCategory(@RequestParam int category, @PathVariable String type) {
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
    public Entity saveEntity(@RequestBody Map<String, Object> data, @PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return employeeService.saveEmployee(objectMapper.convertValue(data, EmployeeEntity.class));
            case "company":
                return companyService.saveCompany(objectMapper.convertValue(data, CompanyEntity.class));
            case "office":
                return officeService.saveOffice(objectMapper.convertValue(data, OfficeEntity.class));
            case "staff":
                return staffService.saveStaff(objectMapper.convertValue(data, StaffEntity.class));
            case "working_conditions":
                return workingConditionsService.saveWorkingConditions(objectMapper.convertValue(data, WorkingConditionsEntity.class));
            case "qualifications":
                return qualificationsService.saveQualifications(objectMapper.convertValue(data, QualificationsEntity.class));
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    /**
     * IDリストのEntityを削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/{type}/delete")
	@ResponseBody
    public Entity deleteEntityByIds(@RequestBody List<SimpleData> ids, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
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
     * 指定したIDのEntityを削除する
     * @param ID
     * @return 
     */
    @PostMapping("/{type}/delete/id")
    @ResponseBody
    public Entity deleteEntityById(@RequestParam int id, @AuthenticationPrincipal OidcUser principal, @PathVariable String type) {
        String userName = principal.getAttribute("preferred_username");
        switch (type.toLowerCase()) {
            case "qualifications":
                return qualificationsService.deleteQualificationsById(id, userName);
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
    public List<Entity> getCompanyCombo(@PathVariable String type) {
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

    /**
     * IDから資格情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/qualifications/get/id/{type}")
	@ResponseBody
    public List<Entity> getQualificationsById(@RequestParam int id, @PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return qualificationsService.getQualificationsByEmployeeId(id);
            case "company":
                return qualificationsService.getQualificationsByCompanyId(id);
            default:
                return null;
        }
        
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/qualifications/get/all/{type}")
	@ResponseBody
    public List<Entity> getQualificationsList(@PathVariable String type) {
        switch (type.toLowerCase()) {
            case "employee":
                return qualificationsService.getEmployeeQualificationsList();
            case "company":
                return qualificationsService.getCompanyQualificationsList();
            default:
                return null;
        }
    }

    /**
     * 今日のEntityListを取得
     * @return
     */
    @GetMapping("/{type}/get/today")
    @ResponseBody
    public List<Entity> getTodaysAttendanceDataForAllEmployees(@PathVariable String type) {
        switch (type.toLowerCase()) {
            case "timeworks":
                return timeworksService.getListOfAllEmployeesToday();
            default:
                return null;
        }
    }
}