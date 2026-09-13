package com.kyouseipro.neo.domain.operations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.sql.repository.SqlRepository;
class OperationAttachmentAccessTest {
 @Test void certificateDirectFileAndGroupRoutesRequirePrivatePermission(){
  var sql=mock(SqlRepository.class);var access=mock(OperationAccess.class);var repo=mock(OperationRepository.class);
  var policy=new OperationAttachmentAccess(sql,access,repo);
  when(sql.selectMap(anyString(),anyList())).thenReturn(List.of(Map.of("parentType","OP_QUALIFICATION","parentId",12)));
  assertThrows(AccessDeniedException.class,()->policy.file(1,false));assertThrows(AccessDeniedException.class,()->policy.group(1,true));
  when(access.privateAccess()).thenReturn(true);when(access.canWrite(OperationSpec.QUALIFICATION)).thenReturn(true);
  assertTrue(policy.file(1,false));assertTrue(policy.group(1,true));
  assertFalse(policy.parent("ORDER",1,false));
 }
}
