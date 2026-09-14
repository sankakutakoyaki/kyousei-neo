package com.kyouseipro.neo.domain.corporation.api.office;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.kyouseipro.neo.sql.repository.SqlRepository;
class OwnOfficeContextTest {
 @Test void selectsOnlyUnambiguousActiveOwnOffice(){
  var sql=mock(SqlRepository.class);var controller=new OwnOfficeContextController(sql);
  var auth=new UsernamePasswordAuthenticationToken("employee@example.test","unused");
  when(sql.selectMap(contains("FROM offices"),anyList())).thenReturn(List.of(Map.of("value",2,"label","大阪")));
  when(sql.selectMap(contains("FROM employees"),eq(List.of("employee@example.test")))).thenReturn(List.of(Map.of("officeId",2)));
  assertEquals(2,controller.context(auth).get("defaultOfficeId"));
  when(sql.selectMap(contains("FROM employees"),anyList())).thenReturn(List.of(Map.of("officeId",2),Map.of("officeId",3)));
  assertEquals("",controller.context(auth).get("defaultOfficeId"));
  when(sql.selectMap(contains("FROM employees"),anyList())).thenReturn(List.of(Map.of("officeId",9)));
  assertEquals("",controller.context(auth).get("defaultOfficeId"));
 }
}
