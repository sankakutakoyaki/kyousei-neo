package com.kyouseipro.neo.domain.business.dispatch;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import jakarta.servlet.http.HttpServletRequest;
class DispatchServiceTest {
 @Test void oldWriteEndpointCannotChangeLegacyAssignments() {
  var sql=mock(SqlRepository.class);
  var service=new DispatchService(sql,new DispatchLogSqlProvider(),mock(HttpServletRequest.class));
  assertThrows(BusinessException.class,()->service.save(Map.of()));
  verifyNoInteractions(sql);
 }
}
