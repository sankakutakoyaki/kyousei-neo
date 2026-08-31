package com.kyouseipro.neo.domain.business.api.order;

public record OrderPdfImportResult(
        long orderImportId,
        long primeConstractorId,
        String originalFileName,
        String storedFileName) {
}
