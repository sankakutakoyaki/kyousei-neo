
package com.kyouseipro.neo.sql.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.model.SelectRequest;

class DispatchScopeLockTest {

    @Test
    void トランザクション外ではロック取得を拒否する() {
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DispatchScopeLock lock = new DispatchScopeLock(queryExecutor);

        assertThrows(IllegalStateException.class,
            () -> lock.lock("2026-11-18", 1, 1));

        // DBへの問い合わせが実行されないことを確認
        verifyNoInteractions(queryExecutor);
    }

    @Test
    void ロック取得成功時は正常終了する() {
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DispatchScopeLock lock = new DispatchScopeLock(queryExecutor);

        // SQL Serverがロック取得成功（0）を返す
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenReturn(List.of(Map.of("lockResult", 0)));

        // Springのトランザクション状態をテスト用に設定
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            assertDoesNotThrow(() -> lock.lock("2026-11-18", 1, 1));

            // 日付・営業所・配車区分から正しいロック名が生成されることを確認
            ArgumentCaptor<SelectRequest> captor = ArgumentCaptor.forClass(SelectRequest.class);
            verify(queryExecutor).select(any(QueryDefinition.class), captor.capture());

            assertEquals(
                "dispatch:2026-11-18:1:1",
                captor.getValue().getParams().get("resource")
            );
        } finally {
            // 他のテストへの影響を防ぐ
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    @Test
    void ロック取得失敗時は例外を返す() {
        QueryExecutor queryExecutor = mock(QueryExecutor.class);
        DispatchScopeLock lock = new DispatchScopeLock(queryExecutor);

        // SQL Serverのロック取得タイムアウトを再現
        when(queryExecutor.select(any(QueryDefinition.class), any(SelectRequest.class)))
            .thenReturn(List.of(Map.of("lockResult", -1)));

        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            BusinessException error = assertThrows(BusinessException.class,
                () -> lock.lock("2026-11-18", 1, 1));

            assertEquals(
                "他の担当者が配車情報を更新しています。再度お試しください。",
                error.getMessage()
            );
        } finally {
            // テスト用のトランザクション状態を解除
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }
}
