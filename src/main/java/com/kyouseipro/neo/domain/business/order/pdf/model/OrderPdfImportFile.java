package com.kyouseipro.neo.domain.business.order.pdf.model;

import java.nio.file.Path;

public record OrderPdfImportFile(
        String originalFileName,
        Path path) {
}
