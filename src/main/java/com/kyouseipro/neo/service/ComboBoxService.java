package com.kyouseipro.neo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeComboEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepositry;

@Service
public class ComboBoxService {
    /**
     * 性別のリスト
     * @return
     */
    public static List<IEntity> getGender() {
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
    public static List<IEntity> getBloodType() {
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
    public static List<IEntity> getEmployeeCategory() {
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
    public static List<IEntity> getPaymentMethod() {
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
    public static List<IEntity> getPayType() {
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
    public static List<IEntity> getCompany() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id as number, name as text FROM companies WHERE NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new SimpleData());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * すべての会社リスト
     * @return
     */
    public static List<IEntity> getClient() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id as number, name as text FROM companies WHERE NOT (state = " + Enums.state.DELETE.getNum() + ") AND NOT (category = 0);");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new SimpleData());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * すべての営業所リスト
     * @return
     */
    public static List<IEntity> getOffice() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT company_id, office_id, name as office_name FROM offices WHERE NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new OfficeComboEntity());
        return SqlRepositry.getEntityList(sqlData);
    }

    /**
     * すべての資格リスト
     * @return
     */
    public static List<IEntity> getQualificationMaster() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT qualification_master_id as number, name as text FROM qualification_master WHERE NOT (state = " + Enums.state.DELETE.getNum() + ");");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new SimpleData());
        return SqlRepositry.getEntityList(sqlData);
    }
}
