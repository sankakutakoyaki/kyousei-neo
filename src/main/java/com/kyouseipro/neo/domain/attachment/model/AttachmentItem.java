package com.kyouseipro.neo.domain.attachment.model;

public record AttachmentItem(
    long attachmentId, String displayName, String fileType, String mimeType,
    long fileSize, Integer width, Integer height) {}
