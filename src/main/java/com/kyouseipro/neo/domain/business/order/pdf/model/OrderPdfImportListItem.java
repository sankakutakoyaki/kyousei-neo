package com.kyouseipro.neo.domain.business.order.pdf.model;

import java.time.LocalDateTime;

public record OrderPdfImportListItem(
        long orderImportId,
        String originalFileName,
        long fileSize,
        LocalDateTime registDate) {
}
