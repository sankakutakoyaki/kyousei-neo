package com.kyouseipro.neo.domain.attachment.model;

import java.util.List;

public record AttachmentGroup(long attachmentGroupId, String groupName, List<AttachmentItem> files) {}
