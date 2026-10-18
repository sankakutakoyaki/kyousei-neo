package com.kyouseipro.neo.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.kyouseipro.neo.common.enums.code.OrderCategory;
import com.kyouseipro.neo.common.enums.code.RecycleCategory;
import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.interfaces.sql.QueryBuilderPart;
import com.kyouseipro.neo.interfaces.sql.SqlProviderPart;
import com.kyouseipro.neo.sql.model.SelectRequest;
import com.kyouseipro.neo.sql.provider.QueryBuilderProvider;
import com.kyouseipro.neo.sql.provider.SqlProvider;
import com.kyouseipro.neo.sql.query.business.order.OrderItemListQueryBuilder;

class ProviderScanTest {

    @Test
    void scanSqlProviderParts() throws Exception {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(
            new AssignableTypeFilter(SqlProviderPart.class)
        );

        List<Class<?>> classes = new ArrayList<>();

        for (var bean : scanner.findCandidateComponents("com.kyouseipro.neo")) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());

                // テスト用クラスは除外
                if (clazz.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toString()
                        .contains("test-classes")) {
                    continue;
                }

                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        classes.sort(Comparator.comparing(Class::getName));

        System.out.println("\n=== SqlProviderPart Scan ===");

        for (Class<?> clazz : classes) {
            System.out.println(clazz.getSimpleName());
        }

        System.out.println("Provider数: " + classes.size());

        assertFalse(classes.isEmpty(), "SqlProviderPartが見つかりません。");

        List<SqlProviderPart> providers = new ArrayList<>();

        for (Class<?> clazz : classes) {
            try {
                SqlProviderPart provider = (SqlProviderPart)
                    clazz.getDeclaredConstructor().newInstance();

                providers.add(provider);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(
                    "Providerの生成に失敗しました: " + clazz.getName(), e
                );
            }
        }

        SqlProvider sqlProvider = new SqlProvider(providers);

        QueryPermissionDiagnosisService service =
            new QueryPermissionDiagnosisService(
                sqlProvider,
                new QueryPermissionValidator()
            );

        var issues = service.diagnose();

        System.out.println("\n=== All Query Permission Diagnosis ===");

        for (var issue : issues) {
            System.out.println(
                issue.queryId() + " : " + issue.message()
            );
        }

        System.out.println("登録QueryId数: " + sqlProvider.getDefinitions().size());
        System.out.println("未設定件数: " + issues.size());
        // ===== 権限対象の抽出 =====
        var tables = new java.util.TreeSet<String>();
        var operations = new java.util.TreeSet<String>();

        for (var def : sqlProvider.getDefinitions().values()) {
            if (def.getPermissionTable() != null
                    && !def.getPermissionTable().isBlank()) {
                tables.add(def.getPermissionTable());
            }

            if (def.getTableMeta() != null) {
                tables.add(def.getTableMeta().tableName());
            }

            if (def.getPermissionOperation() != null
                    && !def.getPermissionOperation().isBlank()) {
                operations.add(def.getPermissionOperation());
            }
        }

        System.out.println("\n=== Permission Tables ===");
        tables.forEach(System.out::println);

        System.out.println("\n=== Permission Operations ===");
        operations.forEach(System.out::println);

        System.out.println("権限対象テーブル数: " + tables.size());
        System.out.println("業務操作権限数: " + operations.size());
        assertTrue(issues.isEmpty(), issues.toString());
    }

    @Test
    void scanQueryBuilderParts() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(QueryBuilderPart.class));

        List<Class<?>> classes = new ArrayList<>();

        for (var bean : scanner.findCandidateComponents("com.kyouseipro.neo")) {
            try {
                Class<?> clazz = Class.forName(bean.getBeanClassName());

                // テスト用クラスは除外
                if (clazz.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toString()
                        .contains("test-classes")) {
                    continue;
                }

                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        assertFalse(classes.isEmpty(), "QueryBuilderPartが見つかりません。");

        List<QueryBuilderPart> parts = new ArrayList<>();

        try (var context = new AnnotationConfigApplicationContext()) {
            // 現在の依存クラスを登録
            context.register(OrderItemListQueryBuilder.class);

            for (Class<?> clazz : classes) {
                context.register(clazz);
            }

            context.refresh();

            parts.addAll(context.getBeansOfType(QueryBuilderPart.class).values());
        }

        QueryBuilderProvider provider = new QueryBuilderProvider(parts);
        QueryPermissionValidator validator = new QueryPermissionValidator();

        var builders = provider.getBuilders();
        var issues = new ArrayList<String>();

        for (var entry : builders.entrySet()) {
            QueryId queryId = entry.getKey();

            Map<String, Object> params = new HashMap<>();

            switch (queryId) {
                case ORDER_LIST ->
                    params.put("category", OrderCategory.VISIT.getCode());

                case RECYCLE_LIST ->
                    params.put("category", RecycleCategory.REGIST.getCode());

                default -> {
                }
            }

            SelectRequest req = new SelectRequest();
            req.setParams(params);

            try {
                var definition = entry.getValue().build(req);

                for (String message : validator.validate(definition)) {
                    issues.add(queryId.getId() + " : " + message);
                }
            } catch (Exception e) {
                issues.add(queryId.getId() + " : 生成失敗 - " + e.getMessage());
            }
        }

        issues.sort(String::compareTo);

        System.out.println("\n=== Dynamic Query Permission Diagnosis ===");

        for (String issue : issues) {
            System.out.println(issue);
        }

        System.out.println("動的QueryId数: " + builders.size());
        System.out.println("未設定件数: " + issues.size());
        assertFalse(builders.isEmpty(), "QueryBuilderが登録されていません。");
        assertTrue(issues.isEmpty(), String.join("\n", issues));
    }
}