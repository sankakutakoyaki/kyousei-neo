package com.kyouseipro.neo.sql.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfSystemProperty(
    named = "log.schema.integration",
    matches = "true"
)
class LogTableSchemaIntegrationTest {

    @Autowired
    private LogTableSchemaService schemaService;
    @Autowired
    private SqlTableMetadataService metadataService;

    @Test
    void diagnoseCompanies() {
        var result = schemaService.diagnose("companies");

        System.out.println("テーブル: " + result.tableName());
        System.out.println("Logテーブル存在: " + result.logTableExists());
        System.out.println("CREATE SQL:\n" + result.createSql());
        System.out.println("ALTER SQL:\n" + result.alterSql());
        System.out.println("定義不一致: " + result.mismatches());

        assertTrue(result.logTableExists());
        assertTrue(result.alterSql().isBlank());
        assertTrue(result.mismatches().isEmpty());
    }

    @Test
    void diagnoseAllLogTables() {
        var tables = metadataService.getTablesWithLog();
        int errors = 0;

        for (String table : tables) {
            try {
                var result = schemaService.diagnose(table);

                boolean ok = result.logTableExists()
                    && result.alterSql().isBlank()
                    && result.mismatches().isEmpty();

                System.out.println(
                    table + " : " + (ok ? "OK" : "要確認")
                );

                if (!ok) {
                    errors++;
                    System.out.println("ALTER SQL:\n" + result.alterSql());
                    System.out.println("定義不一致: " + result.mismatches());
                }
            } catch (Exception e) {
                errors++;
                System.out.println(table + " : ERROR - " + e.getMessage());
            }
        }

        assertEquals(0, errors, "ログテーブルに問題があります");
    }

    @Test
    void generateItemMasterLogTableSql() {
        var result = schemaService.diagnose("item_masters");

        assertFalse(result.logTableExists(),
            "item_masters_log は既に存在しています");

        System.out.println("\n===== item_masters_log 作成SQL =====");
        System.out.println(result.createSql());
        System.out.println("===================================\n");
    }
}