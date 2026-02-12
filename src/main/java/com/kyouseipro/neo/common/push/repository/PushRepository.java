package com.kyouseipro.neo.common.push.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.push.entity.SubscriptionRequest;
import com.kyouseipro.neo.common.push.mapper.SubscriptionRequestMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

import lombok.Data;

@Data
@Repository
public class PushRepository {
    private final SqlRepository sqlRepository;
    private record param(SubscriptionRequest s, String editor) {}
    /**
     * エンドポイントが一致するSubscriptionを取得する
     * @param endpoint
     * @return　見つからなければNullを返す
     */
    public SubscriptionRequest findByEndpoint(String endpoint){
        return sqlRepository.queryOneOrNull(
            "SELECT * FROM subscriptions WHERE endpoint = ?",
            (ps, ep) -> ps.setString(1, ep),
            SubscriptionRequestMapper::map,
            endpoint
        );
    }

    /**
     * 未登録のSubscription情報をDBに登録する
     * @param subscription
     * @return
     */
    @Transactional
    public int save(SubscriptionRequest subscription, String editor){
        return sqlRepository.queryOne(
            """
            INSERT INTO subscriptions (endpoint, p256dh, auth, username) VALUES (?,?,?,?);
            DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;SELECT @NEW_ID as subscription_id;""",
            (ps, e) -> {
                int index = 1;
                ps.setString(index++, e.s.getEndpoint());
                ps.setString(index++, e.s.getP256dh());
                ps.setString(index++, e.s.getAuth());
                ps.setString(index++, e.editor);
            },
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
            new param(subscription, editor)
        );
    }
    
    /**
     * 登録してあるすべてのSubscriptionを取得する
     * @return
     */
    public List<SubscriptionRequest> findAll() {
        return sqlRepository.queryList(
            "SELECT * FROM subscriptions ",
            null,
            SubscriptionRequestMapper::map,
            null
        );
    }

    /**
     * 指定したEndpointのSubscriptionをDBから削除する
     * @param endpoint
     * @return
     */
    @Transactional
    public int deleteByEndpoint(String endpoint, String editor){
        int count = sqlRepository.queryOne(
            """
                DELETE FROM subscriptions WHERE endpoint = ?;
                DECLARE @ROW_COUNT int; SET @ROW_COUNT = @@ROWCOUNT;SELECT @ROW_COUNT as subscription_id
            """,
            (ps, e) -> {
                int index = 1;
                ps.setString(index++, endpoint);
            },
            null,
            endpoint
        );

        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}
