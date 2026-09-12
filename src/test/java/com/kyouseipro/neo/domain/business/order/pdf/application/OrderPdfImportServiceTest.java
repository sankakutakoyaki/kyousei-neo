package com.kyouseipro.neo.domain.business.order.pdf.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.config.UploadConfig;
import com.kyouseipro.neo.domain.ai.application.AiLearningDataService;
import com.kyouseipro.neo.domain.business.order.ocr.LocalOcrService;
import com.kyouseipro.neo.domain.business.order.ocr.ai.OrderAiExtractionClient;
import com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLayoutRepository;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportResult;
import com.kyouseipro.neo.domain.business.order.pdf.repository.OrderPdfImportRepository;

class OrderPdfImportServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void savesPdfUnderTheSelectedPrimeConstractorDirectory() throws Exception {
        OrderPdfImportService service = new OrderPdfImportService(
            new UploadConfig(temporaryDirectory.toString()),
            repositoryReturning(1L),
            mock(LocalOcrService.class),
            mock(OrderAiExtractionClient.class),
            mock(com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLogRepository.class),
            mock(OrderOcrLayoutRepository.class),
            new ObjectMapper()
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
        assertEquals(1L, result.orderImportId());
        assertEquals("order.pdf", result.originalFileName());
        assertTrue(Files.exists(savedFile));
        assertEquals("%PDF-1.7\nexample", Files.readString(savedFile));
    }

    @Test
    void rejectsUnselectedPrimeConstractorValueZero() {
        OrderPdfImportService service = new OrderPdfImportService(
            new UploadConfig(temporaryDirectory.toString()),
            repositoryReturning(1L),
            mock(LocalOcrService.class),
            mock(OrderAiExtractionClient.class),
            mock(com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLogRepository.class),
            mock(OrderOcrLayoutRepository.class),
            new ObjectMapper()
        );
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "order.pdf",
            "application/pdf",
            "%PDF-1.7".getBytes(StandardCharsets.US_ASCII)
        );

        assertThrows(BusinessException.class, () -> service.save("0", file));
    }

    @Test
    void savesInitialExtractionAndReturnsItsActualMetadata() throws Exception {
        var repository = repositoryReturning(12L);
        var logs = mock(com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLogRepository.class);
        var client = mock(OrderAiExtractionClient.class);
        Path pdf = temporaryDirectory.resolve("original.pdf");
        Files.writeString(pdf, "%PDF-1.7");
        when(repository.findFile(12L)).thenReturn(new com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportFile("original.pdf", Path.of("original.pdf")));
        when(repository.findPrimeConstractorId(12L)).thenReturn(1085L);
        java.util.Map<String, Object> candidates = java.util.Map.of("customerName", "山田", "items", java.util.List.of(java.util.Map.of("itemModel", "A")));
        when(client.extract(pdf, 1085L)).thenReturn(java.util.Optional.of(new OrderAiExtractionClient.ExtractionResponse(candidates, "actual-model", "heiwado-20260912-01")));
        when(logs.find(12L)).thenReturn(null);
        when(logs.insert(org.mockito.ArgumentMatchers.eq(12L), anyString(), anyString(), anyString())).thenReturn(7L);
        var service = new OrderPdfImportService(new UploadConfig(temporaryDirectory.toString()), repository,
            mock(LocalOcrService.class), client, logs, mock(OrderOcrLayoutRepository.class), new ObjectMapper());
        var result = service.recognizeHeiwado(12L);
        assertEquals(7L, result.get("ocrLogId"));
        assertEquals("actual-model", result.get("modelName"));
        org.mockito.Mockito.verify(logs).insert(12L, new ObjectMapper().writeValueAsString(candidates), "actual-model", "heiwado-20260912-01");
        org.mockito.Mockito.verify(repository, org.mockito.Mockito.never()).saveOcrResult(anyLong(), anyString());
    }

    @Test
    void retryReusesInitialLogWithoutCallingAi() throws Exception {
        var repository = repositoryReturning(12L);
        var logs = mock(com.kyouseipro.neo.domain.business.order.ocr.repository.OrderOcrLogRepository.class);
        var client = mock(OrderAiExtractionClient.class);
        Files.writeString(temporaryDirectory.resolve("original.pdf"), "%PDF-1.7");
        when(repository.findFile(12L)).thenReturn(new com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportFile("original.pdf", Path.of("original.pdf")));
        java.util.Map<String, Object> original = java.util.Map.of("ocrLogId", 7L, "candidates", java.util.Map.of("customerName", "初回"));
        when(logs.find(12L)).thenReturn(original);
        var service = new OrderPdfImportService(new UploadConfig(temporaryDirectory.toString()), repository,
            mock(LocalOcrService.class), client, logs, mock(OrderOcrLayoutRepository.class), new ObjectMapper());
        assertEquals(original, service.recognizeHeiwado(12L));
        org.mockito.Mockito.verifyNoInteractions(client);
    }

    private OrderPdfImportRepository repositoryReturning(long orderImportId) {
        OrderPdfImportRepository repository = mock(OrderPdfImportRepository.class);
        when(repository.insert(anyLong(), anyString(), anyString(), anyString(), anyString(), anyLong()))
            .thenReturn(orderImportId);
        return repository;
    }
}
