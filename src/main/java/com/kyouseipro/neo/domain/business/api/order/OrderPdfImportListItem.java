package com.kyouseipro.neo.domain.business.api.order;

import java.time.LocalDateTime;

public record OrderPdfImportListItem(
        long orderImportId,
        String originalFileName,
        long fileSize,
        LocalDateTime registDate) {
}
