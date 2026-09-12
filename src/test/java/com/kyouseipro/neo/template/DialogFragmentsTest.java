package com.kyouseipro.neo.template;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

class DialogFragmentsTest {
    String render(String path, Map<String,Object> data) {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); resolver.setSuffix(".html");
        var engine = new SpringTemplateEngine(); engine.setTemplateResolver(resolver);
        var context = new Context(); context.setVariable("data", data);
        return engine.process(path, context);
    }
    @Test void existingFooterKeepsTwoButtons() {
        var html = render("fragments/dialog/footer", Map.of("footerId","existing-footer"));
        assertTrue(html.contains("name=\"cancelBtn\""));
        assertTrue(html.contains("name=\"submitBtn\""));
        assertFalse(html.contains("footerCloseBtn"));
    }
    @Test void arrivalFooterUsesSharedButtonsAndOptionalClose() {
        var html = render("fragments/dialog/footer", Map.of("footerId","arrival-footer", "submitText","訂正", "cancelText","取消", "closeText","閉じる"));
        assertTrue(html.contains(">訂正</button>"));
        assertTrue(html.contains(">取消</button>"));
        assertTrue(html.contains("name=\"footerCloseBtn\""));
    }
    @Test void sharedHeaderProvidesCloseButton() {
        var html = render("fragments/dialog/header", Map.of("title","入荷登録・履歴"));
        assertTrue(html.contains("入荷登録・履歴"));
        assertTrue(html.contains("name=\"closeBtn\""));
        assertTrue(html.contains("/icons/close-s.png"));
    }
}
