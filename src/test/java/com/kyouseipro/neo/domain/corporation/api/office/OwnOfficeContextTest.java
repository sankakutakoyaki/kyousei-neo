package com.kyouseipro.neo.domain.corporation.api.office;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.kyouseipro.neo.common.combo.entity.ComboDto;

class OwnOfficeContextTest {

    @Test
    void selectsOnlyUnambiguousActiveOwnOffice() {

        var officeService =
            mock(OfficeService.class);

        var ownOfficeContextService =
            mock(OwnOfficeContextService.class);

        var controller =
            new OwnOfficeContextController(
                officeService,
                ownOfficeContextService
            );

        var auth =
            new UsernamePasswordAuthenticationToken(
                "employee@example.test",
                "unused"
            );

        when(
            officeService.findComboByCategory(anyInt())
        ).thenReturn(
            List.of(
                new ComboDto(
                    2L,
                    "大阪"
                )
            )
        );

        when(
            ownOfficeContextService.getOfficeId(auth)
        ).thenReturn(2);

        assertEquals(
            2,
            controller.context(auth)
                .get("defaultOfficeId")
        );

        when(
            ownOfficeContextService.getOfficeId(auth)
        ).thenReturn(null);

        assertEquals(
            "",
            controller.context(auth)
                .get("defaultOfficeId")
        );

        when(
            ownOfficeContextService.getOfficeId(auth)
        ).thenReturn(9);

        assertEquals(
            "",
            controller.context(auth)
                .get("defaultOfficeId")
        );
    }

    @Test
    void headquartersIsIdentifiedByOfficeId1000() {

        var officeService =
            mock(OfficeService.class);

        var ownOfficeContextService =
            mock(OwnOfficeContextService.class);

        var controller =
            new OwnOfficeContextController(
                officeService,
                ownOfficeContextService
            );

        var auth =
            new UsernamePasswordAuthenticationToken(
                "test",
                "unused"
            );

        when(
            officeService.findComboByCategory(anyInt())
        ).thenReturn(
            List.of(
                new ComboDto(
                    1000L,
                    "本社"
                )
            )
        );

        when(
            ownOfficeContextService.getOfficeId(auth)
        ).thenReturn(1000);

        var result =
            controller.context(auth);

        assertEquals(
            true,
            result.get("isHeadOffice")
        );

        assertEquals(
            1000,
            result.get("defaultOfficeId")
        );
    }
}