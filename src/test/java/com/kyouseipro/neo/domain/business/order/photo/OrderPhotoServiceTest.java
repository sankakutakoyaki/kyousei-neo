package com.kyouseipro.neo.domain.business.order.photo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import com.kyouseipro.neo.security.*;
import com.kyouseipro.neo.domain.attachment.application.AttachmentService;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.common.exception.BusinessException;
class OrderPhotoServiceTest {
    final AttachmentService attachments=mock(AttachmentService.class);final SqlRepository sql=mock(SqlRepository.class);
    final OrderScopeGuard guard=mock(OrderScopeGuard.class);final OrderAccessService access=mock(OrderAccessService.class);
    final PermissionService permissions=mock(PermissionService.class);final OwnOfficeContextService context=mock(OwnOfficeContextService.class);
    final Authentication auth=mock(Authentication.class);
    final OrderPhotoService photos=new OrderPhotoService(attachments,sql,guard,context);
    @Test void uploadsCheckOrderAccessBeforeStorage(){
        doThrow(new AccessDeniedException("denied")).when(guard).require(auth,5,false);
        assertThrows(AccessDeniedException.class,()->photos.upload(auth,5,"COMPLETED",LocalDate.now(),List.of()));
        verifyNoInteractions(attachments);
    }
    @Test void unsupportedImagesAndMissingDatesDoNotWrite(){
        var file=new MockMultipartFile("files","photo.heic","image/heic",new byte[]{1});
        assertThrows(BusinessException.class,()->photos.upload(auth,5,"COMPLETED",LocalDate.now(),List.of(file)));
        assertThrows(BusinessException.class,()->photos.upload(auth,5,"COMPLETED",null,List.of(file)));
        verifyNoInteractions(attachments);
    }
    @Test void galleryRestrictsScopeAndPaginatesWithoutLoadingAllPhotos(){
        when(guard.readCondition(auth)).thenReturn(new OrderAccessService.AccessCondition("OWN",4,null));
        photos.list(auth,null,null,null,null,100);
        verify(sql).selectMap(argThat(q->q.contains("o.own_office_id = ?") && q.contains("FETCH NEXT 101")),argThat(p->p.get(10).equals(4) && p.get(p.size()-1).equals(100)));
    }
}
