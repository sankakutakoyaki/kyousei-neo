
package com.kyouseipro.neo.sql.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@EnabledIfSystemProperty(named = "dispatch.lock.integration", matches = "true")
class DispatchScopeLockIntegrationTest {
    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;
    @Autowired
    private DispatchScopeLock dispatchScopeLock;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void 実DBのトランザクション内で共通ロックを取得できる() {
        // 実DBで取得し、トランザクション終了時に自動解放する
        assertDoesNotThrow(() -> transactionTemplate.executeWithoutResult(status ->
            dispatchScopeLock.lock("2026-11-18", 1, 1)
        ));
    }
}
