package com.kyouseipro.neo.domain.business.api.order;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.response.SimpleResponse;

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
}
