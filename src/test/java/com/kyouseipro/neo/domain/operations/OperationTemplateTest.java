package com.kyouseipro.neo.domain.operations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import jakarta.servlet.http.HttpServletRequest;
class OperationTemplateTest {
 @Test void allOperationPagesRenderSharedFormsAndNoHiddenSensitiveFields() throws Exception {
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
  engine.setLinkBuilder(new org.thymeleaf.linkbuilder.StandardLinkBuilder(){@Override protected String computeContextPath(org.thymeleaf.context.IExpressionContext context,String base,Map<String,Object> parameters){return "";}});
  var access=mock(OperationAccess.class);when(access.privateAccess()).thenReturn(true);when(access.manager()).thenReturn(true);when(access.canWrite(any())).thenReturn(true);
  var controller=new OperationPageController(access);
  for(var spec:OperationSpec.values()) {
   var request=mock(HttpServletRequest.class);when(request.getServletPath()).thenReturn("/"+spec.route);
   var model=new ExtendedModelMap();controller.page(request,model);
   String html=engine.process("fragments/pages/operations/content",new Context(Locale.JAPAN,model));
   assertFalse(html.contains("data-required=\"false\""));assertFalse(html.contains("data-required=\"true\""));assertTrue(html.contains("class=\"normal-table\""));assertTrue(html.contains("id=\"operation-form\""));assertTrue(html.contains("id=\"operation-footer\""));
   assertEquals(spec==OperationSpec.CREW||spec==OperationSpec.SCORE,html.contains("id=\"operation-day\""));
   if(spec==OperationSpec.VEHICLE) {
    assertTrue(html.contains("<textarea"));assertTrue(html.contains("id=\"op-remarks\""));
    assertTrue(html.contains("vehicle-type-options"));assertTrue(html.contains("軽バン"));
    assertTrue(html.indexOf("id=\"op-code\"")<html.indexOf("id=\"op-name\""));
   }
   if(spec==OperationSpec.LABOR) assertFalse(html.contains("certificate-number"));
   preview(spec.route,html,"operationPage.js");
  }
  String order=engine.process("fragments/pages/business/order/content",new Context(Locale.JAPAN));
  assertTrue(order.contains("order-own-office-edit"));assertTrue(order.contains("order-own-office"));
  preview("order",order,"../business/order/orderPage.js");
  preview("timeworks",engine.process("fragments/pages/management/timeworks/content",new Context(Locale.JAPAN)),"../management/timeworksPage.js");
  var model=new ExtendedModelMap();controller.dispatch(model,0);
  String html=engine.process("fragments/pages/operations/dispatch",new Context(Locale.JAPAN,model));
  assertTrue(html.contains("plan-leader-code"));assertTrue(html.contains("plan-legacy"));preview("dispatch",html,"dispatchPage.js");
 }
 private void preview(String name,String html,String module) throws Exception {
  // Opt-in local browser fixture. No production data or real API connections.
  String dir=System.getProperty("operation.preview.dir");if(dir==null)return;
  Files.createDirectories(Path.of(dir));
  Files.writeString(Path.of(dir,name+".html"),"<!doctype html><html><head><meta charset='utf-8'><meta name='_csrf' content='fixture'><meta name='_csrf_header' content='X-CSRF-TOKEN'><meta name='viewport' content='width=device-width,initial-scale=1'><link rel='stylesheet' href='/css/common/default.css'><link rel='stylesheet' href='/css/pages/operations.css'></head><body>"+html+"<script>window.APP={security:{},cache:{}};</script><script type='module'>import {initMobileReadOnly} from '/js/core/access/mobileReadOnly.js';import {init} from '/js/pages/operations/"+module+"';initMobileReadOnly();await init();</script></body></html>");
 }
}
