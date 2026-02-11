package com.kyouseipro.neo.common.push.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.push.entity.SubscriptionRequest;
import com.kyouseipro.neo.common.push.mapper.SubscriptionRequestMapper;
import com.kyouseipro.neo.dto.sql.repository.SqlRepository;

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
    public SubscriptionRequest findByEndpoint(String endpoint){
        String sql = "SELECT * FROM subscriptions WHERE endpoint = ?";

        // return sqlRepository.executeQuery(
        //     sql,
        //     (ps, p) -> {
        //         int index = 1;
        //         ps.setString(index++, endpoint);
        //     },
        //     rs -> rs.next() ? SubscriptionRequestMapper.map(rs) : null,
        //     endpoint
        // );
        return sqlRepository.query(
            sql,
            ps -> {
                int index = 1;
                ps.setString(index++, endpoint);
            },
            rs -> rs.next() ? SubscriptionRequestMapper.map(rs) : null
        );
    }

    /**
     * 未登録のSubscription情報をDBに登録する
     * @param subscription
     * @return
     */
    public int save(SubscriptionRequest subscription, String editor){
        String sql = """
            INSERT INTO subscriptions (endpoint, p256dh, auth, username) VALUES (?,?,?,?);
            DECLARE @NEW_ID int; SET @NEW_ID = @@IDENTITY;SELECT @NEW_ID as subscription_id;""";

        return sqlRepository.executeRequired(
            sql,
            (ps, s) -> {
                int index = 1;
                ps.setString(index++, s.getEndpoint());
                ps.setString(index++, s.getP256dh());
                ps.setString(index++, s.getAuth());

                ps.setString(index++, editor);
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
            subscription
        );
    }
    
    /**
     * 登録してあるすべてのSubscriptionを取得する
     * @return
     */
    public List<SubscriptionRequest> findAll() {
        String sql = "SELECT * FROM subscriptions ";

        return sqlRepository.findAll(
            sql,
            null,
            SubscriptionRequestMapper::map // ← ここで ResultSet を map
        );
    }

    /**
     * 指定したEndpointのSubscriptionをDBから削除する
     * @param endpoint
     * @return
     */
    public int deleteByEndpoint(String endpoint, String editor){
        String sql = """
            DELETE FROM subscriptions WHERE endpoint = ?;
            DECLARE @ROW_COUNT int; SET @ROW_COUNT = @@ROWCOUNT;SELECT @ROW_COUNT as subscription_id;""";

        // return result; // 成功件数。0なら削除なし
        int count = sqlRepository.executeUpdate(
            sql,
            ps -> {
                int index = 1;
                ps.setString(index++, endpoint);
            }
        );
        if (count == 0) {
            throw new BusinessException("他のユーザーにより更新されたか、対象が存在しません。再読み込みしてください。");
        }

        return count;
    }
}
