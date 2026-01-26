package com.kyouseipro.neo.service.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.repository.corporation.CompanyListRepository;
import com.kyouseipro.neo.repository.corporation.OfficeListRepository;
import com.kyouseipro.neo.repository.corporation.StaffListRepository;
import com.kyouseipro.neo.repository.qualification.QualificationsRepository;
import com.kyouseipro.neo.repository.recycle.RecycleRepository;
import com.kyouseipro.neo.repository.work.WorkItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComboBoxService {
    private final CompanyListRepository companyListRepository;
    private final OfficeListRepository officeListRepository;
    private final StaffListRepository staffListRepository;
    private final QualificationsRepository qualificationsRepository;
    private final RecycleRepository recycleRepository;
    private final WorkItemRepository workItemRepository;

    /**
     * 性別のリスト
     * @return
     */
    public List<SimpleData> getGender() {
        List<SimpleData> list = new ArrayList<>();
        for(Enums.gender ent: Enums.gender.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getCode());
            simpleData.setText(ent.getDescription());
            list.add(simpleData);
        }
        return list;
    }
    
    /**
     * 血液型のリスト
     * @return
     */
    public List<SimpleData> getBloodType() {
        List<SimpleData> list = new ArrayList<>();
        for(Enums.bloodType ent: Enums.bloodType.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getCode());
            simpleData.setText(ent.getDescription());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 雇用形態のリスト
     * @return
     */
    public List<SimpleData> getEmployeeCategory() {
        List<SimpleData> list = new ArrayList<>();
        for(Enums.employeeCategory ent: Enums.employeeCategory.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getCode());
            simpleData.setText(ent.getDescription());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 支払い形態のリスト
     * @return
     */
    public List<SimpleData> getPaymentMethod() {
        List<SimpleData> list = new ArrayList<>();
        for(Enums.paymentMethod ent: Enums.paymentMethod.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getCode());
            simpleData.setText(ent.getDescription());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 血液型のリスト
     * @return
     */
    public List<SimpleData> getPayType() {
        List<SimpleData> list = new ArrayList<>();
        for(Enums.payType ent: Enums.payType.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getCode());
            simpleData.setText(ent.getDescription());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 分類リスト　商品：材料：備品：返品
     * @return
     */
    public List<SimpleData> getItemClass() {
        List<SimpleData> list = new ArrayList<>();
        for(Enums.ItemClass ent: Enums.ItemClass.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getCode());
            simpleData.setText(ent.getDescription());
            list.add(simpleData);
        }
        return list;
    }

    // /**
    //  * すべての会社リスト
    //  * @return
    //  */
    // public List<SimpleData> getCompany() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT company_id as number, name as text FROM companies WHERE NOT (state = " + Enums.state.DELETE.getCode() + ") ORDER BY name_kana;");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new SimpleData());
    //     return sqlRepository.getEntityList(sqlData);
    // }
    public List<SimpleData> getOwnCompanyList() {
        return companyListRepository.findAllComboOwnCompany();
    }

    public List<SimpleData> getCompanyList() {
        return companyListRepository.findAllCombo();
    }

    public List<SimpleData> getClientList() {
        return companyListRepository.findAllClientCombo();
    }

    public List<SimpleData> getPrimeConstractorList() {
        return companyListRepository.findAllPrimeConstractorCombo();
    }

    public List<SimpleData> getPrimeConstractorListAddTopOfOwnCompany() {
        List<SimpleData> companyList = companyListRepository.findAllPrimeConstractorCombo();
        return addOwnCompanyTopOfComboList(companyList);
    }

    public List<SimpleData> getPrimeConstractorListAddTopOfOwnCompanyHasOriginalPrice() {
        List<SimpleData> companyList = companyListRepository.findAllPrimeConstractorComboHasOriginalPrice();
        return addOwnCompanyTopOfComboList(companyList);
    }

    public List<SimpleData> getCompanyListByCategory(int category) {
        return companyListRepository.findAllComboByCategory(category);
    }

    public List<OfficeListEntity> getOfficeList() {
        return officeListRepository.findAll();
    }

    public List<OfficeListEntity> getOfficeListOrderByKana() {
        return officeListRepository.findAllOrderByKana();
    }

    public List<StaffListEntity> getSalesStaffList() {
        return staffListRepository.findBySalesStaff();
    }

    public List<SimpleData> getSimpleOfficeList() {
        return officeListRepository.findAllCombo();
    }

    public List<SimpleData> getSimpleOwnOfficeList() {
        return officeListRepository.findByOwnCombo();
    }

    public List<SimpleData> getQualificationMaster() {
        return qualificationsRepository.findAllByQualificationMasterCombo();
    }

    public List<SimpleData> getLicenseMaster() {
        return qualificationsRepository.findAllByLicenseMasterCombo();
    }

    public List<SimpleData> getWorkItemParentCategoryList() {
        return workItemRepository.findParentCategoryCombo();
    }

    public List<SimpleData> addOwnCompanyTopOfComboList(List<SimpleData> list) {
        // 自社を取得
        List<SimpleData> ownList = getOwnCompanyList();
        // 自社を先頭に追加
        for (SimpleData simpleData : ownList) {
            list.add(0, simpleData);
        }
        return list;        
    }

    public List<SimpleData> getRecycleGroupList() {
        return recycleRepository.findGroupCombo();
    }

    // /**
    //  * すべての会社リスト
    //  * @return
    //  */
    // public List<SimpleData> getClient() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT company_id as number, name as text FROM companies WHERE NOT (state = " + Enums.state.DELETE.getCode() + ") AND NOT (category = 0) ORDER BY name_kana;");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new SimpleData());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * すべての営業所リスト
    //  * @return
    //  */
    // public List<SimpleData> getOffice() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT company_id, office_id, name as office_name FROM offices WHERE NOT (state = " + Enums.state.DELETE.getCode() + ") ORDER BY name_kana;");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new OfficeComboEntity());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * すべての資格リスト
    //  * @return
    //  */
    // public List<SimpleData> getQualificationMaster() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = " + Enums.state.DELETE.getCode() + ") ORDER BY code;");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new SimpleData());
    //     return sqlRepository.getEntityList(sqlData);
    // }

    // /**
    //  * すべての許可証リスト
    //  * @return
    //  */
    // public List<SimpleData> getLicenseMaster() {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = " + Enums.state.DELETE.getCode() + ") AND category_name = '許可' ORDER BY code;");
    //     SqlData sqlData = new SqlData();
    //     sqlData.setData(sb.toString(), new SimpleData());
    //     return sqlRepository.getEntityList(sqlData);
    // }
}
