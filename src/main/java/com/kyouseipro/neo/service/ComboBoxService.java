package com.kyouseipro.neo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.data.SimpleData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComboBoxService {
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
