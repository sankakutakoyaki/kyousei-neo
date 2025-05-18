package com.kyouseipro.neo.entity.data;

import com.kyouseipro.neo.interfaceis.IEntity;

import lombok.Data;

@Data
public class SqlData {

    private String sqlString = "";
    private String classPath = "";

    /**
     * 主エンティティのパスを取得してセットする
     * @param entity
     */
    public void setPath(IEntity entity) {
        if (entity == null) return;
        this.classPath = entity.getClass().getName();
    }

    /**
     * SQL文とクラスパスをセットする
     * @param sqlString
     * @param entity
     */
    public void setData(String sqlString, IEntity entity) {
        setSqlString(sqlString);
        setPath(entity);
    }
}
