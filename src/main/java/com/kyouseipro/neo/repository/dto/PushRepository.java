package com.kyouseipro.neo.repository.dto;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.entity.dto.SubscriptionRequest;
import com.kyouseipro.neo.mapper.dto.SubscriptionRequestMapper;
import com.kyouseipro.neo.query.parameter.common.PushParameterBinder;
import com.kyouseipro.neo.query.sql.common.PushSqlBuilder;
import com.kyouseipro.neo.repository.common.SqlRepository;

import lombok.Data;

@Data
@Repository
public class PushRepository {
    private final SqlRepository sqlRepository;

    /**
     * エンドポイントが一致するSubscriptionを取得する
     * @param endpoint
     * @return　見つからなければNullを返す
     */
    public Optional<SubscriptionRequest> findByEndpoint(String endpoint){
        String sql = PushSqlBuilder.buildFindByEndpoint();

        return sqlRepository.executeQuery(
            sql,
            (ps, en) -> PushParameterBinder.bindFindByEndpoint(ps, en),
            rs -> rs.next() ? SubscriptionRequestMapper.map(rs) : null,
            endpoint
        );
    }

    /**
     * 未登録のSubscription情報をDBに登録する
     * @param subscription
     * @return
     */
    public int save(SubscriptionRequest subscription, String editor){
        String sql = PushSqlBuilder.buildInsertSubscription();

        // // return sqlRepositry.execute(
        // //     sql,
        // //     (pstmt, sub) -> PushParameterBinder.bindInsertSubscription(pstmt, sub, editor),
        // //     rs -> rs.next() ? rs.getInt("subscrioption_id") : null,
        // //     subscription
        // // );
        // try {
            return sqlRepository.executeRequired(
                sql,
                (ps, en) -> PushParameterBinder.bindInsertSubscription(ps, en, editor),
                rs -> {
                    if (!rs.next()) {
                        throw new BusinessException("登録に失敗しました");
                    }
                    int id = rs.getInt("subscription_id");

                    if (rs.next()) {
                        throw new IllegalStateException("ID取得結果が複数行です");
                    }
                    return id;
                },
                subscription
            );
        // } catch (RuntimeException e) {
        //     if (SqlExceptionUtil.isDuplicateKey(e)) {
        //         throw new BusinessException("このコードはすでに使用されています。");
        //     }
        //     throw e;
        // }
    }
    
    /**
     * 登録してあるすべてのSubscriptionを取得する
     * @return
     */
    public List<SubscriptionRequest> findAll() {
        String sql = PushSqlBuilder.buildFindAll();

        return sqlRepository.findAll(
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
    public int deleteByEndpoint(String endpoint, String editor){
        String sql = PushSqlBuilder.buildDeleteSubscriptionForEndpoint();

        // Integer result = sqlRepository.executeUpdate(
        //     sql,
        //     ps -> PushParameterBinder.bindDeleteSubscriptionForEndpoint(ps, endpoint, editor)
        // );

        // return result; // 成功件数。0なら削除なし
        int count = sqlRepository.executeUpdate(
            sql,
            ps -> PushParameterBinder.bindDeleteSubscriptionForEndpoint(ps, endpoint, editor)
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}
