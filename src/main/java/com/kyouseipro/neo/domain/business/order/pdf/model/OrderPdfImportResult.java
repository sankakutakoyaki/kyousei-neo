package com.kyouseipro.neo.domain.business.order.pdf.model;

public record OrderPdfImportResult(
        long orderImportId,
        long primeConstractorId,
        String originalFileName,
        String storedFileName) {
}
