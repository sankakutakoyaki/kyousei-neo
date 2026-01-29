package com.kyouseipro.neo.service.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.interfaceis.CodeEnum;
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

    private <E extends Enum<E> & CodeEnum> List<SimpleData> toSimpleDataList(E[] values) {
    List<SimpleData> list = new ArrayList<>();
        for (E ent : values) {
            SimpleData sd = new SimpleData();
            sd.setNumber(ent.getCode());
            sd.setText(ent.getDescription());
            list.add(sd);
        }
        return list;
    }

    /**
     * 性別
     * @return
     */
    public List<SimpleData> getGender() {
        return toSimpleDataList(Enums.gender.values());
    }

    /**
     * 血液型
     * @return
     */
    public List<SimpleData> getBloodType() {
        return toSimpleDataList(Enums.bloodType.values());
    }

    /**
     * 雇用形態
     * @return
     */
    public List<SimpleData> getEmployeeCategory() {
        return toSimpleDataList(Enums.employeeCategory.values());
    }

    /**
     * 支払い携帯
     * @return
     */
    public List<SimpleData> getPaymentMethod() {
        return toSimpleDataList(Enums.paymentMethod.values());
    }

    /**
     * 給与携帯
     * @return
     */
    public List<SimpleData> getPayType() {
        return toSimpleDataList(Enums.payType.values());
    }

    /**
     * 分類リスト　商品：材料：備品：返品
     * @return
     */
    public List<SimpleData> getItemClass() {
        return toSimpleDataList(Enums.ItemClass.values());
    }

    // /**
    //  * 性別のリスト
    //  * @return
    //  */
    // public List<SimpleData> getGender() {
    //     List<SimpleData> list = new ArrayList<>();
    //     for(Enums.gender ent: Enums.gender.values()) {
    //         SimpleData simpleData = new SimpleData();
    //         simpleData.setNumber(ent.getCode());
    //         simpleData.setText(ent.getDescription());
    //         list.add(simpleData);
    //     }
    //     return list;
    // }
    
    // /**
    //  * 血液型のリスト
    //  * @return
    //  */
    // public List<SimpleData> getBloodType() {
    //     List<SimpleData> list = new ArrayList<>();
    //     for(Enums.bloodType ent: Enums.bloodType.values()) {
    //         SimpleData simpleData = new SimpleData();
    //         simpleData.setNumber(ent.getCode());
    //         simpleData.setText(ent.getDescription());
    //         list.add(simpleData);
    //     }
    //     return list;
    // }

    // /**
    //  * 雇用形態のリスト
    //  * @return
    //  */
    // public List<SimpleData> getEmployeeCategory() {
    //     List<SimpleData> list = new ArrayList<>();
    //     for(Enums.employeeCategory ent: Enums.employeeCategory.values()) {
    //         SimpleData simpleData = new SimpleData();
    //         simpleData.setNumber(ent.getCode());
    //         simpleData.setText(ent.getDescription());
    //         list.add(simpleData);
    //     }
    //     return list;
    // }

    // /**
    //  * 支払い形態のリスト
    //  * @return
    //  */
    // public List<SimpleData> getPaymentMethod() {
    //     List<SimpleData> list = new ArrayList<>();
    //     for(Enums.paymentMethod ent: Enums.paymentMethod.values()) {
    //         SimpleData simpleData = new SimpleData();
    //         simpleData.setNumber(ent.getCode());
    //         simpleData.setText(ent.getDescription());
    //         list.add(simpleData);
    //     }
    //     return list;
    // }

    // /**
    //  * 血液型のリスト
    //  * @return
    //  */
    // public List<SimpleData> getPayType() {
    //     List<SimpleData> list = new ArrayList<>();
    //     for(Enums.payType ent: Enums.payType.values()) {
    //         SimpleData simpleData = new SimpleData();
    //         simpleData.setNumber(ent.getCode());
    //         simpleData.setText(ent.getDescription());
    //         list.add(simpleData);
    //     }
    //     return list;
    // }

    // /**
    //  * 分類リスト　商品：材料：備品：返品
    //  * @return
    //  */
    // public List<SimpleData> getItemClass() {
    //     List<SimpleData> list = new ArrayList<>();
    //     for(Enums.ItemClass ent: Enums.ItemClass.values()) {
    //         SimpleData simpleData = new SimpleData();
    //         simpleData.setNumber(ent.getCode());
    //         simpleData.setText(ent.getDescription());
    //         list.add(simpleData);
    //     }
    //     return list;
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
}
