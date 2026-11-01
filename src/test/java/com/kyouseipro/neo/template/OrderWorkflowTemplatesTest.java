package com.kyouseipro.neo.template;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.context.Context;
import java.util.*;
class OrderWorkflowTemplatesTest {
    String render(String name){
        var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
        return engine.process(name,new Context());
    }
    @Test void officeHasOutcomeFooterAndPhotoGalleryWithNoDuplicateIds(){
        var html=render("fragments/pages/business/order/content");
        assertTrue(html.contains("data-order-outcome-open"));assertTrue(html.contains("data-order-outcome-dialog"));
        assertTrue(html.contains("id=\"order-photo-gallery\""));
        unique(html);
    }
    @Test void driverHasHtmlCameraControlsAndIndependentPhotoViewer(){
        var html=render("fragments/pages/business/myorder/content");
        assertTrue(html.contains("id=\"myorder-photos\""));assertTrue(html.contains("capture=\"environment\""));
        assertTrue(html.contains("data-photo-day"));unique(html);
    }
    void unique(String html){var ids=new HashSet<String>();var matcher=java.util.regex.Pattern.compile("\\sid=\"([^\"]+)\"").matcher(html);while(matcher.find())assertTrue(ids.add(matcher.group(1)),matcher.group(1));}
}
