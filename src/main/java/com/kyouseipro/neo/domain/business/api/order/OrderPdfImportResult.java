package com.kyouseipro.neo.domain.business.api.order;

public record OrderPdfImportResult(
        long primeConstractorId,
        String originalFileName,
        String storedFileName) {
}
