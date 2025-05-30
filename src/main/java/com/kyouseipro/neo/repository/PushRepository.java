package com.kyouseipro.neo.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.data.SubscriptionRequest;
import com.kyouseipro.neo.interfaceis.Entity;

import lombok.Data;

@Repository
@Data
public class PushRepository {
    private final SqlRepository sqlRepositry;

    /**
     * エンドポイントが一致するSubscriptionを取得する
     * @param endpoint
     * @return　見つからなければNullを返す
     */
    public SubscriptionRequest findByEndpoint(String endpoint){
        String sqlStr = SubscriptionRequest.getSelectString() + " WHERE endpoint = '" + endpoint + "'";
        SqlData sqlData = new SqlData();
        sqlData.setData(sqlStr, new SubscriptionRequest());
        SubscriptionRequest entity = (SubscriptionRequest)sqlRepositry.getEntity(sqlData);
        if (entity.getSubscription_id() == 0) {
            return null;
        } else {
            return entity;
        }
    }

    /**
     * 未登録のSubscription情報をDBに登録する
     * @param subscription
     * @return
     */
    public boolean save(SubscriptionRequest subscription){
        String sqlStr = SubscriptionRequest.getSelectString() + " WHERE endpoint = '" + subscription.getEndpoint() + "'";
        sqlStr += "DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;SELECT @NEW_ID as number, '' as text;";
        SqlData sqlData = new SqlData();
        sqlData.setData(sqlStr, new SubscriptionRequest());
        SubscriptionRequest entity = (SubscriptionRequest)sqlRepositry.getEntity(sqlData);
        if (entity.getSubscription_id() == 0) {
            SimpleData result = (SimpleData)sqlRepositry.excuteSqlString(subscription.getInsertString());
            if (result.getNumber() > 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 登録してあるすべてのSubscriptionを取得する
     * @return
     */
    public List<Entity> getList() {
        SqlData sqlData = new SqlData();
        sqlData.setData(SubscriptionRequest.getSelectString(), new SubscriptionRequest());
        return sqlRepositry.getEntityList(sqlData);
    }

    /**
     * 指定したEndpointのSubscriptionをDBから削除する
     * @param endpoint
     * @return
     */
    public boolean deleteByEndpoint(String endpoint){
        String sqlStr = "DELETE FROM subscriptions WHERE endpoint = '" + endpoint + "';";
        sqlStr += "DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;SELECT @NEW_ID as number, '' as text;";
        SimpleData result = (SimpleData)sqlRepositry.excuteSqlString(sqlStr);
        if (result.getNumber() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
