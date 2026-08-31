package com.kyouseipro.neo.domain.business.api.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.config.UploadConfig;

class OrderPdfImportServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void savesPdfUnderTheSelectedPrimeConstractorDirectory() throws Exception {
        OrderPdfImportService service = new OrderPdfImportService(
            new UploadConfig(temporaryDirectory.toString())
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "order.pdf",
            "application/pdf",
            "%PDF-1.7\nexample".getBytes(StandardCharsets.US_ASCII)
        );

        OrderPdfImportResult result = service.save("123", file);

        Path savedFile = temporaryDirectory
            .resolve("order-pdf")
            .resolve("123")
            .resolve(result.storedFileName());
        assertEquals(123L, result.primeConstractorId());
        assertEquals("order.pdf", result.originalFileName());
        assertTrue(Files.exists(savedFile));
        assertEquals("%PDF-1.7\nexample", Files.readString(savedFile));
    }

    @Test
    void rejectsUnselectedPrimeConstractorValueZero() {
        OrderPdfImportService service = new OrderPdfImportService(
            new UploadConfig(temporaryDirectory.toString())
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "order.pdf",
            "application/pdf",
            "%PDF-1.7".getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(BusinessException.class, () -> service.save("0", file));
    }
}
