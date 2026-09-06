package com.kyouseipro.neo.domain.business.order.pdf.application;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.Map;
import java.sql.Connection;
import javax.sql.DataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.domain.business.order.pdf.repository.OrderImportRegistrationRepository;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import com.kyouseipro.neo.sql.provider.Tables;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.aop.framework.ProxyFactory;

class OrderImportRegistrationServiceTest {
    final OrderImportRegistrationRepository imports=mock(OrderImportRegistrationRepository.class);
    final BaseSqlRepository base=mock(BaseSqlRepository.class);
    final OrderImportRegistrationService service=new OrderImportRegistrationService(imports,base,new ObjectMapper());
    Map<String,String> values() {return Map.of("customerName","確認済み氏名","address","確認済み住所","requestedDate","2026-09-06",
        "items","[{\"itemModel\":\"ABC\",\"itemQuantity\":\"1\"}]",
        "works","[{\"orderWorkName\":\"リサイクル運搬\",\"orderWorkQuantity\":\"1\"}]");}
    void ready() {
        when(imports.lock(12)).thenReturn(new OrderImportRegistrationRepository.ImportLock(1085,null));
        when(imports.reviewId(12)).thenReturn(20L);
        when(base.insert(eq(Tables.ORDER_BY_IDS),anyMap(),eq("editor"))).thenReturn(1001L);
        when(base.insert(eq(Tables.ORDER_ITEM_BY_IDS),anyMap(),eq("editor"))).thenReturn(1L);
        when(base.insert(eq(Tables.ORDER_WORK_BY_IDS),anyMap(),eq("editor"))).thenReturn(2L);
    }
    @Test void insertsAllTablesAndLinksImportAndConfirmedReview() {
        ready(); var result=service.register(12,values(),"editor");
        assertEquals(1001,result.orderId());assertFalse(result.alreadyRegistered());
        verify(base).insert(eq(Tables.ORDER_ITEM_BY_IDS),argThat(p->p.get("orderId").equals(1001L)),eq("editor"));
        verify(base).insert(eq(Tables.ORDER_WORK_BY_IDS),argThat(p->p.get("orderId").equals(1001L)),eq("editor"));
        verify(imports).finish(eq(12L),eq(1001L),eq(20L),contains("確認済み氏名"),eq("editor"));
    }
    @Test void retryReturnsExistingOrderWithoutInsertingOrOverwriting() {
        when(imports.lock(12)).thenReturn(new OrderImportRegistrationRepository.ImportLock(1085,1001L));
        assertTrue(service.register(12,values(),"editor").alreadyRegistered());
        verifyNoInteractions(base);verify(imports,never()).finish(anyLong(),anyLong(),anyLong(),anyString(),anyString());
    }
    @Test void childFailureRollsBackSpringTransaction() throws Exception {
        ready(); when(base.insert(eq(Tables.ORDER_WORK_BY_IDS),anyMap(),anyString())).thenThrow(new IllegalStateException("write failed"));
        DataSource ds=mock(DataSource.class);Connection connection=mock(Connection.class);
        when(ds.getConnection()).thenReturn(connection);when(connection.getAutoCommit()).thenReturn(true);
        var interceptor=new TransactionInterceptor(new DataSourceTransactionManager(ds),new AnnotationTransactionAttributeSource());
        var factory=new ProxyFactory(service);factory.addAdvice(interceptor);
        var proxy=(OrderImportRegistrationService)factory.getProxy();
        assertThrows(IllegalStateException.class,()->proxy.register(12,values(),"editor"));
        verify(connection).rollback();verify(connection,never()).commit();
        verify(imports,never()).finish(anyLong(),anyLong(),anyLong(),anyString(),anyString());
    }
}
