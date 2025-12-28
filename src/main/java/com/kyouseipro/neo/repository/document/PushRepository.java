package com.kyouseipro.neo.repository.document;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.data.SubscriptionRequest;
import com.kyouseipro.neo.mapper.data.SubscriptionRequestMapper;
import com.kyouseipro.neo.query.parameter.common.PushParameterBinder;
import com.kyouseipro.neo.query.sql.common.PushSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.Data;

@Data
@Repository
public class PushRepository {
    private final SqlRepository sqlRepositry;

    /**
     * エンドポイントが一致するSubscriptionを取得する
     * @param endpoint
     * @return　見つからなければNullを返す
     */
    public SubscriptionRequest findByEndpoint(String endpoint){
        String sql = PushSqlBuilder.buildFindByEndpoint();

        return sqlRepositry.execute(
            sql,
            (pstmt, str) -> PushParameterBinder.bindFindByEndpoint(pstmt, str),
            rs -> rs.next() ? SubscriptionRequestMapper.map(rs) : null,
            endpoint
        );
    }

    /**
     * 未登録のSubscription情報をDBに登録する
     * @param subscription
     * @return
     */
    public Integer save(SubscriptionRequest subscription, String editor){
        String sql = PushSqlBuilder.buildInsertSubscription();

        return sqlRepositry.execute(
            sql,
            (pstmt, sub) -> PushParameterBinder.bindInsertSubscription(pstmt, sub, editor),
            rs -> rs.next() ? rs.getInt("subscrioption_id") : null,
            subscription
        );
    }
    
    /**
     * 登録してあるすべてのSubscriptionを取得する
     * @return
     */
    public List<SubscriptionRequest> findAll() {
        String sql = PushSqlBuilder.buildFindAll();

        return sqlRepositry.findAll(
            sql,
            null,
            // ps -> PushParameterBinder.bindFindAll(ps, null),
            SubscriptionRequestMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 指定したEndpointのSubscriptionをDBから削除する
     * @param endpoint
     * @return
     */
    public Integer deleteByEndpoint(String endpoint, String editor){
        String sql = PushSqlBuilder.buildDeleteSubscriptionForEndpoint();

        Integer result = sqlRepositry.executeUpdate(
            sql,
            ps -> PushParameterBinder.bindDeleteSubscriptionForEndpoint(ps, endpoint, editor)
        );

        return result; // 成功件数。0なら削除なし
    }
}
