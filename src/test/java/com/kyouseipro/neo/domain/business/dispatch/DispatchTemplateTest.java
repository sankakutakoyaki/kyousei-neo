package com.kyouseipro.neo.domain.business.dispatch;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class DispatchTemplateTest {
    @Test void rendersSharedDialogsAndManagementFlag() {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); resolver.setSuffix(".html");
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(resolver);
        // 共通ヘッダーのコンテキスト相対URLを非Webテストでも解決する。
        engine.setLinkBuilder(new org.thymeleaf.linkbuilder.StandardLinkBuilder() {
            @Override protected String computeContextPath(org.thymeleaf.context.IExpressionContext context, String base, java.util.Map<String,Object> parameters) { return ""; }
        });
        for (boolean manager : new boolean[]{true,false}) {
            var context = new Context(); context.setVariable("dispatchManager", manager);
            String html = engine.process("fragments/pages/business/dispatch/content", context);
            assertTrue(html.contains("data-dispatch-manager=\"" + manager + "\""));
            assertTrue(html.contains("name=\"closeBtn\""));
            assertTrue(html.contains("id=\"dispatch-footer\""));
            assertTrue(html.contains("id=\"msg-dialog\""));
            assertTrue(html.contains("id=\"dispatch-search\""));
            assertTrue(html.contains("class=\"normal-table\""));
            assertTrue(html.contains("id=\"dispatch-results\""));
            assertTrue(html.contains("class=\"normal-input\""));
            assertTrue(html.contains("class=\"normal-select\""));
            assertTrue(html.contains("class=\"date-range\""));
            assertTrue(html.contains("class=\"table-footer\""));
        }
    }
}
