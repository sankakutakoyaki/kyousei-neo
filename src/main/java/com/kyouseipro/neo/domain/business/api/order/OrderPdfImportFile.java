package com.kyouseipro.neo.domain.business.api.order;

import java.nio.file.Path;

public record OrderPdfImportFile(
        String originalFileName,
        Path path) {
}
