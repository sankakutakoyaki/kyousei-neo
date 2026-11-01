package com.kyouseipro.neo.domain.operations.dispatch.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.domain.operations.dispatch.result.WorkResultService;
import com.kyouseipro.neo.domain.management.MyOrderPageController;

class MyOrderAccountAccessTest {
    @Configuration @EnableMethodSecurity(proxyTargetClass=true)
    static class Config {
        @Bean SqlRepository sql(){return mock(SqlRepository.class);}
        @Bean OwnOfficeContextService office(){return mock(OwnOfficeContextService.class);}
        @Bean WorkResultService results(){return mock(WorkResultService.class);}
        @Bean MyOrderController api(SqlRepository sql,OwnOfficeContextService office,WorkResultService results){return new MyOrderController(sql,office,results);}
        @Bean MyOrderPageController page(){return new MyOrderPageController();}
    }
    AnnotationConfigApplicationContext context;
    @BeforeEach void setup(){context=new AnnotationConfigApplicationContext(Config.class);}
    @AfterEach void close(){SecurityContextHolder.clearContext();context.close();}
    UsernamePasswordAuthenticationToken login(String role){
        var auth=new UsernamePasswordAuthenticationToken("account","unused",List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(auth);return auth;
    }
    @Test void everyRoleUsesItsOwnEmployeeForListAndDetail(){
        var sql=context.getBean(SqlRepository.class);var office=context.getBean(OwnOfficeContextService.class);
        var api=context.getBean(MyOrderController.class);var page=context.getBean(MyOrderPageController.class);
        for(String role:List.of("APPROLE_admin","APPROLE_master","APPROLE_leader","APPROLE_staff","APPROLE_user")) {
            reset(sql,office);var auth=login(role);when(office.getEmployeeId(auth)).thenReturn(42L);
            assertTrue(page.getMyOrders().contains("myorder"));
            assertEquals(List.of(),api.find(auth,LocalDate.of(2026,11,1)));
            verify(sql).selectMap(contains("dcm.employee_id = ?"),argThat(p->p.get(2).equals(42L)));
            assertThrows(ResponseStatusException.class,()->api.detail(auth,999L));
            verify(sql).selectMap(contains("WHERE o.order_id = ?"),argThat(p->p.get(0).equals(999L) && p.get(p.size()-1).equals(42L)));
        }
    }
    @Test void missingEmployeeAndAnonymousCannotUseTheApi(){
        var api=context.getBean(MyOrderController.class);var auth=login("APPROLE_admin");
        assertThrows(AccessDeniedException.class,()->api.find(auth,LocalDate.now()));
        verifyNoInteractions(context.getBean(SqlRepository.class));
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key","anonymous",List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        assertThrows(AccessDeniedException.class,()->context.getBean(MyOrderPageController.class).getMyOrders());
    }
}
