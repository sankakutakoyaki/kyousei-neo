package com.kyouseipro.neo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeComboEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComboBoxService {
    private final SqlRepository sqlRepository;
    /**
     * 性別のリスト
     * @return
     */
    public List<IEntity> getGender() {
        List<IEntity> list = new ArrayList<>();
        for(Enums.gender ent: Enums.gender.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getNum());
            simpleData.setText(ent.getStr());
            list.add(simpleData);
        }
        return list;
    }
    
    /**
     * 血液型のリスト
     * @return
     */
    public List<IEntity> getBloodType() {
        List<IEntity> list = new ArrayList<>();
        for(Enums.bloodType ent: Enums.bloodType.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getNum());
            simpleData.setText(ent.getStr());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 雇用形態のリスト
     * @return
     */
    public List<IEntity> getEmployeeCategory() {
        List<IEntity> list = new ArrayList<>();
        for(Enums.employeeCategory ent: Enums.employeeCategory.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getNum());
            simpleData.setText(ent.getStr());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 支払い形態のリスト
     * @return
     */
    public List<IEntity> getPaymentMethod() {
        List<IEntity> list = new ArrayList<>();
        for(Enums.paymentMethod ent: Enums.paymentMethod.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getNum());
            simpleData.setText(ent.getStr());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * 血液型のリスト
     * @return
     */
    public List<IEntity> getPayType() {
        List<IEntity> list = new ArrayList<>();
        for(Enums.payType ent: Enums.payType.values()) {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(ent.getNum());
            simpleData.setText(ent.getStr());
            list.add(simpleData);
        }
        return list;
    }

    /**
     * すべての会社リスト
     * @return
     */
    public List<IEntity> getCompany() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id as number, name as text FROM companies WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") ORDER BY name_kana;");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new SimpleData());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべての会社リスト
     * @return
     */
    public List<IEntity> getClient() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id as number, name as text FROM companies WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0) ORDER BY name_kana;");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new SimpleData());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべての営業所リスト
     * @return
     */
    public List<IEntity> getOffice() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id, office_id, name as office_name FROM offices WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") ORDER BY name_kana;");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeComboEntity());
        return sqlRepository.getEntityList(sqlData);
    }

    /**
     * すべての資格リスト
     * @return
     */
    public List<IEntity> getQualificationMaster() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") ORDER BY name_kana;");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new SimpleData());
        return sqlRepository.getEntityList(sqlData);
    }
}
