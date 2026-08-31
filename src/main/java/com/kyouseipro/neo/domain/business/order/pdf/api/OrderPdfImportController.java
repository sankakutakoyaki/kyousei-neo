package com.kyouseipro.neo.domain.business.order.pdf.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.business.order.pdf.application.OrderPdfImportService;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportFile;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportListItem;
import com.kyouseipro.neo.domain.business.order.pdf.model.OrderPdfImportResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/import")
public class OrderPdfImportController {

    private final OrderPdfImportService orderPdfImportService;

    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimpleResponse<OrderPdfImportResult> importPdf(
            @RequestParam("primeConstractorId") String primeConstractorId,
            @RequestParam("file") MultipartFile file) {
        OrderPdfImportResult result = orderPdfImportService.save(
            primeConstractorId,
            file
        );
        return SimpleResponse.ok("PDFを保存しました。", result);
    }

    @GetMapping
    public List<OrderPdfImportListItem> findByPrimeConstractorId(
            @RequestParam("primeConstractorId") String primeConstractorId) {
        return orderPdfImportService.findByPrimeConstractorId(primeConstractorId);
    }

    @GetMapping("/{orderImportId}/file")
    public ResponseEntity<InputStreamResource> openPdf(
            @PathVariable long orderImportId) throws IOException {
        OrderPdfImportFile file = orderPdfImportService.findFile(orderImportId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(Files.size(file.path()))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline()
                    .filename(file.originalFileName(), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .body(new InputStreamResource(Files.newInputStream(file.path())));
    }

    @PostMapping("/{orderImportId}/ocr/hei-wado")
    public SimpleResponse<Map<String, String>> recognizeHeiwado(@PathVariable long orderImportId) {
        return SimpleResponse.ok("OCR候補を保存しました。", orderPdfImportService.recognizeHeiwado(orderImportId));
    }

    @PostMapping("/{orderImportId}/candidate")
    public SimpleResponse<Void> saveCandidate(
            @PathVariable long orderImportId,
            @RequestBody Map<String, String> candidate) {
        orderPdfImportService.saveCandidate(orderImportId, candidate);
        return SimpleResponse.ok("受注候補を保存しました。", null);
    }

    @GetMapping(value = "/{orderImportId}/preview", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] preview(@PathVariable long orderImportId) {
        return orderPdfImportService.preview(orderImportId);
    }
}
