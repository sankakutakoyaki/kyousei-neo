package com.kyouseipro.neo.template;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class DispatchTemplatesTest {
    @Test void dispatchRendersAllCardTemplatesWithUniqueIdsAndCommonActions() {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); resolver.setSuffix(".html");
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(resolver);
        var html = engine.process("fragments/pages/operations/dispatch/content",new Context());
        for(var id : new String[]{"dispatch-crew-card-template","dispatch-crew-member-template",
            "dispatch-assignment-template","dispatch-order-card-template","dispatch-member-row-template"}) {
            assertTrue(html.contains("id=\""+id+"\""),id);
        }
        var matcher = Pattern.compile("\\bid=\"([^\"]+)\"").matcher(html);
        var ids = new HashSet<String>();
        while(matcher.find()) assertTrue(ids.add(matcher.group(1)),"重複ID: "+matcher.group(1));
        assertTrue(html.contains("data-action=\"toggle-crew-orders\""));
        assertTrue(html.contains("data-action-lock=\"true\""));
        assertTrue(html.contains("data-default=\"today\""));
        assertTrue(html.contains("data-login-office"));
    }
    @Test void masterTabsRenderDistinctHeaderAndButtonIds() {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); resolver.setSuffix(".html");
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(resolver);
        var html = engine.process("fragments/pages/master/content",new Context());
        var matcher = Pattern.compile("\\bid=\"([^\"]+)\"").matcher(html);
        var ids = new HashSet<String>();
        while(matcher.find()) assertTrue(ids.add(matcher.group(1)),"重複ID: "+matcher.group(1));
        for(var id : new String[]{"header-01","header-02","add-btn-01","add-btn-02","reload-btn-01","reload-btn-02"}) {
            assertTrue(ids.contains(id),id);
        }
    }

}
