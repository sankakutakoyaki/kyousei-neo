package com.kyouseipro.neo.common.enums.system;

import java.util.*;
import java.util.stream.Collectors;

public enum QueryId {

    // ===== Company =====
    COMPANY_DETAIL("companyDetail"),
    COMPANY_LIST("companyList"),
    COMPANY_CSV("companyCsv"),
    COMPANY_DELETE_BY_IDS("companyDeleteByIds"),
    COMPANY_SAVE("companySave"),

    // ===== Partner Employee =====
    EMPLOYEE_DETAIL("employeeDetail"),
    EMPLOYEE_LIST("employeeList"),
    EMPLOYEE_CSV("employeeCsv"),
    EMPLOYEE_DELETE_BY_IDS("employeeDeleteByIds"),
    EMPLOYEE_SAVE("employeeSave"),

    // ===== office =====
    OFFICE_DETAIL("officeDetail"),
    OFFICE_LIST("officeList"),
    OFFICE_CSV("officeCsv"),
    OFFICE_DELETE_BY_IDS("officeDeleteByIds"),
    OFFICE_SAVE("officeSave"),

    // ===== Client =====
    CLIENT_LIST("clientList"),
    CLIENT_OFFICE_LIST("clientOfficeList"),

    // ===== Staff =====
    STAFF_DETAIL("staffDetail"),
    STAFF_LIST("staffList"),
    STAFF_CSV("staffCsv"),
    STAFF_DELETE_BY_IDS("staffDeleteByIds"),
    STAFF_SAVE("staffSave");

    private final String id;

    QueryId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    private static final Map<String, QueryId> MAP =
        Arrays.stream(values())
            .collect(Collectors.toMap(
                QueryId::getId,
                v -> v
            ));

    public static QueryId from(String id) {
        if (id == null) {
            throw new IllegalArgumentException("queryIdがnullです");
        }
        QueryId result = MAP.get(id);
        if (result == null) {
            throw new IllegalArgumentException("不正なqueryId: " + id);
        }
        return result;
    }
}