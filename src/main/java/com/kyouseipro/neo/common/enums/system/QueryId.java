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
    EMPLOYEE_RESOLVE("employeeResolve"),

    // ===== office =====
    OFFICE_DETAIL("officeDetail"),
    OFFICE_LIST("officeList"),
    OFFICE_CSV("officeCsv"),
    OFFICE_DELETE_BY_IDS("officeDeleteByIds"),
    OFFICE_SAVE("officeSave"),

    // ===== Vehicle =====
    VEHICLE_DETAIL("vehicleDetail"),
    VEHICLE_LIST("vehicleList"),
    VEHICLE_CSV("vehicleCsv"),
    VEHICLE_DELETE_BY_IDS("vehicleDeleteByIds"),
    VEHICLE_SAVE("vehicleSave"),
    VEHICLE_DEFAULT_MEMBER_LIST("vehicleDefaultMemberList"),
    VEHICLE_DEFAULT_MEMBER_SAVE("vehicleDefaultMemberSave"),
    VEHICLE_DEFAULT_MEMBER_DELETE_BY_IDS("vehicleDefaultMemberDeleteByIds"),
    VEHICLE_DEFAULT_MEMBER_WORKING_LIST("vehicleDefaultMemberWorkingList"),
    VEHICLE_DISPATCH_DEFAULT_LIST("vehicleDispatchDefaultList"),
    VEHICLE_DISPATCH_CATEGORY_LIST("vehicleDispatchCategoryList"),
    VEHICLE_DISPATCH_CATEGORY_DELETE_BY_IDS("vehicleDispatchCategoryDeleteByIds"),
    VEHICLE_DISPATCH_CATEGORY_SAVE("vehicleDispatchCategorySave"),

    // ===== crew =====
    DAILY_CREW_DETAIL("dailyCrewDetail"),
    DAILY_CREW_LIST("dailyCrewList"),
    DAILY_CREW_DELETE_BY_IDS("dailyCrewDeleteByIds"),
    DAILY_CREW_SAVE("dailyCrewSave"),
    DAILY_CREW_BULK_CREATE("dailyCrewBulkCreate"),
    DAILY_CREW_MEMBER_LIST("dailyCrewMemberList"),
    DAILY_CREW_MEMBER_DELETE_BY_IDS("dailyCrewMemberDeleteByIds"),
    DAILY_CREW_MEMBER_SAVE("dailyCrewMemberSave"),
    DAILY_CREW_BOARD_LIST("dailyCrewBoardList"),

    // ===== Dispatch =====
    DISPATCH_ASSIGNMENT_LIST("dispatchAssignmentList"),
    DISPATCH_ASSIGNMENT_NEXT_VISIT_ORDER("dispatchAssignmentNextVisitOrder"),
    DISPATCH_ASSIGNMENT_REORDER("dispatchAssignmentReorder"),
    DISPATCH_ASSIGNMENT_DELETE_BY_IDS("dispatchAssignmentDeleteByIds"),
    DISPATCH_ASSIGNMENT_SAVE("dispatchAssignmentSave"),
    DISPATCH_ORDER_LIST("dispatchOrderList"),    

    // ===== Shift =====
    EMPLOYEE_SHIFT_DETAIL("employeeShiftDetail"),
    EMPLOYEE_SHIFT_LIST("employeeShiftList"),
    EMPLOYEE_SHIFT_WORKING_LIST("employeeShiftWorkingList"),
    EMPLOYEE_SHIFT_DELETE_BY_IDS("employeeShiftDeleteByIds"),
    EMPLOYEE_SHIFT_SAVE("employeeShiftSave"),
    EMPLOYEE_SHIFT_EMPLOYEE_LIST("employeeShiftEmployeeList"),
    EMPLOYEE_SHIFT_MONTH_LIST("employeeShiftMonthList"),

    // ===== Client =====
    CLIENT_LIST("clientList"),
    CLIENT_OFFICE_LIST("clientOfficeList"),

    // ===== Staff =====
    STAFF_DETAIL("staffDetail"),
    STAFF_LIST("staffList"),
    STAFF_CSV("staffCsv"),
    STAFF_DELETE_BY_IDS("staffDeleteByIds"),
    STAFF_SAVE("staffSave"),

    // ===== Recycle =====
    RECYCLE_DETAIL("recycleDetail"),
    RECYCLE_LIST("recycleList"),
    RECYCLE_CSV("recycleCsv"),
    RECYCLE_DELETE_BY_IDS("recycleDeleteByIds"),
    RECYCLE_SAVE("recycleSave"),
    RECYCLE_DELIVERY_SAVE("recycleDeliverySave"),
    RECYCLE_SHIPPING_SAVE("recycleShippingSave"),
    RECYCLE_LOSS_SAVE("recycleLossSave"),

    // ===== RecycleMaker =====
    RECYCLE_MAKER_DETAIL("recycleMakerDetail"),
    RECYCLE_MAKER_LIST("recycleMakerList"),
    RECYCLE_MAKER_CSV("recycleMakerCsv"),
    RECYCLE_MAKER_DELETE_BY_IDS("recycleMakerDeleteByIds"),
    RECYCLE_MAKER_SAVE("recycleMakerSave"),

    // ===== RecycleManufacturer =====
    RECYCLE_MANUFACTURER_DETAIL("recycleManufacturerDetail"),
    RECYCLE_MANUFACTURER_LIST("recycleManufacturerList"),
    RECYCLE_MANUFACTURER_CSV("recycleManufacturerCsv"),
    RECYCLE_MANUFACTURER_DELETE_BY_IDS("recycleManufacturerDeleteByIds"),
    RECYCLE_MANUFACTURER_SAVE("recycleManufacturerSave"),

    // ===== RecyclePrice =====
    RECYCLE_PRICE_DETAIL("recyclePriceDetail"),
    RECYCLE_PRICE_LIST("recyclePriceList"),
    RECYCLE_PRICE_CSV("recyclePriceCsv"),
    RECYCLE_PRICE_DELETE_BY_IDS("recyclePriceDeleteByIds"),
    RECYCLE_PRICE_SAVE("recyclePriceSave"),

    // ===== WorkSkill =====
    WORK_SKILL_DETAIL("workSkillDetail"),
    WORK_SKILL_LIST("workSkillList"),
    WORK_SKILL_CSV("workSkillCsv"),
    WORK_SKILL_DELETE_BY_IDS("workSkillDeleteByIds"),
    WORK_SKILL_SAVE("workSkillSave"),

    // ===== RecycleItem =====
    RECYCLE_ITEM_LIST("recycleItemList"),

    // ===== Order =====
    ORDER_DETAIL("orderDetail"),
    ORDER_LIST("orderList"),
    ORDER_CSV("orderCsv"),
    ORDER_DELETE_BY_IDS("orderDeleteByIds"),
    ORDER_SAVE("orderSave"),
    ORDER_ITEM_FORM_LIST("orderItemFormList"),
    ORDER_WORK_FORM_LIST("orderWorkFormList"),

    // ===== OrderItem =====
    ORDER_ITEM_DETAIL("orderItemDetail"),
    ORDER_ITEM_LIST("orderItemList"),
    ORDER_ITEM_LIST_BY_ITEM_MODEL("orderItemListByItemModel"),
    ORDER_ITEM_CSV("orderItemCsv"),
    ORDER_ITEM_DELETE_BY_IDS("orderItemDeleteByIds"),
    ORDER_ITEM_SAVE("orderItemSave"),
    ORDER_ITEM_ARRIVAL("orderItemArrival"),
    ORDER_ITEM_ARRIVAL_DETAIL("orderItemArrivalDetail"),
    ORDER_ITEM_ARRIVAL_CORRECT("orderItemArrivalCorrect"),
    ORDER_ITEM_ARRIVAL_CANCEL("orderItemArrivalCancel"),
    ORDER_ITEM_CREATE("orderItemCreate"),

    // ===== OrderWork =====
    ORDER_WORK_DETAIL("orderWorkDetail"),
    ORDER_WORK_LIST("orderWorkList"),
    ORDER_WORK_CSV("orderWorkCsv"),
    ORDER_WORK_DELETE_BY_IDS("orderWorkDeleteByIds"),
    ORDER_WORK_SAVE("orderWorkSave"),

    // ===== Master =====
    WORK_MASTER_DETAIL("workMasterDetail"),
    WORK_MASTER_LIST("workMasterList"),
    WORK_MASTER_CSV("workMasterCsv"),
    WORK_MASTER_DELETE_BY_IDS("workMasterDeleteByIds"),
    WORK_MASTER_SAVE("workMasterSave"),
    ITEM_MASTER_DETAIL("itemMasterDetail"),
    ITEM_MASTER_LIST("itemMasterList"),
    ITEM_MASTER_FIND_BY_JAN_CODE("itemMasterFindByJanCode"),
    ITEM_MASTER_CSV("itemMasterCsv"),
    ITEM_MASTER_DELETE_BY_IDS("itemMasterDeleteByIds"),
    ITEM_MASTER_SAVE("itemMasterSave");

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
