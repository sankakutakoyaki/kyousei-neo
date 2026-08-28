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