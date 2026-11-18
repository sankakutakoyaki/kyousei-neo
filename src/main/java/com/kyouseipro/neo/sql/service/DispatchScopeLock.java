
package com.kyouseipro.neo.sql.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DispatchScopeLock {

    private final QueryExecutor queryExecutor;

    /**
     * 日付・営業所・配車区分単位の排他ロックを取得する。
     * トランザクション終了時にSQL Serverが自動解放する。
     */
    public void lock(String workDate, int officeId, int dispatchCategory) {
        // トランザクション外での実行を防止
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("配車範囲ロックにはトランザクションが必要です。");
        }

        String resource = "dispatch:" + workDate + ":" + officeId + ":" + dispatchCategory;

        // QueryDefinition query = QueryDefinition.select(
        //     """
        //     DECLARE @result INT;
        //     EXEC @result = sp_getapplock
        //         @Resource = ?,
        //         @LockMode = 'Exclusive',
        //         @LockOwner = 'Transaction',
        //         @LockTimeout = 10000;
        //     SELECT @result AS lock_result;
        //     """,
        //     List.of("resource")
        // );
        // QueryDefinition query = QueryDefinition.select(
        //     """
        //     DECLARE @result INT;
        //     DECLARE @resource NVARCHAR(255) = CAST(? AS NVARCHAR(255));

        //     EXEC @result = sp_getapplock
        //         @Resource = @resource,
        //         @LockMode = 'Exclusive',
        //         @LockOwner = 'Transaction',
        //         @LockTimeout = 10000;

        //     SELECT @result AS lock_result;
        //     """,
        //     List.of("resource")
        // );

        QueryDefinition query = QueryDefinition.select(
            """
            DECLARE @result INT;
            DECLARE @probe BIGINT;
            DECLARE @resource NVARCHAR(255) = CAST(? AS NVARCHAR(255));

            -- SQL Server側のトランザクションを開始させる
            SELECT TOP (1) @probe = daily_crew_id
            FROM daily_crews;

            EXEC @result = sp_getapplock
                @Resource = @resource,
                @LockMode = 'Exclusive',
                @LockOwner = 'Transaction',
                @LockTimeout = 10000;

            SELECT @result AS lock_result;
            """,
            List.of("resource")
        );

        SelectRequest req = new SelectRequest();
        req.setParams(Map.of("resource", resource));

        List<Map<String, Object>> rows = queryExecutor.select(query, req);

        // 戻り値が負の場合、ロック取得失敗として処理を中止
        if (rows.isEmpty() || !(rows.get(0).get("lockResult") instanceof Number result)
                || result.intValue() < 0) {
            throw new BusinessException("他の担当者が配車情報を更新しています。再度お試しください。");
        }
    }
}
