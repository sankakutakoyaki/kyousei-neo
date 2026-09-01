package com.kyouseipro.neo.domain.attachment.model;

import java.nio.file.Path;

public record AttachmentFile(String displayName, String mimeType, Path path) {}
