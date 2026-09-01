package com.kyouseipro.neo.domain.attachment.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.domain.attachment.application.AttachmentService;
import com.kyouseipro.neo.domain.attachment.model.AttachmentFile;
import com.kyouseipro.neo.domain.attachment.model.AttachmentGroup;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class AttachmentController {
    private final AttachmentService service;
    public record NameRequest(String name) {}

    @GetMapping("/{parentType}/{parentId}/groups")
    public List<AttachmentGroup> groups(@PathVariable String parentType,@PathVariable long parentId) { return service.findGroups(parentType,parentId); }
    @PostMapping("/{parentType}/{parentId}/groups")
    public SimpleResponse<Long> createGroup(@PathVariable String parentType,@PathVariable long parentId,@RequestBody NameRequest request) {
        return SimpleResponse.ok("フォルダを作成しました。",service.createGroup(parentType,parentId,request.name()));
    }
    @PatchMapping("/groups/{groupId}")
    public SimpleResponse<Void> renameGroup(@PathVariable long groupId,@RequestBody NameRequest request) { service.renameGroup(groupId,request.name()); return SimpleResponse.ok("名前を変更しました。",null); }
    @DeleteMapping("/groups/{groupId}")
    public SimpleResponse<Void> deleteGroup(@PathVariable long groupId) { service.deleteGroup(groupId); return SimpleResponse.ok("フォルダを削除しました。",null); }
    @PostMapping(value="/{parentType}/{parentId}/groups/{groupId}/files",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimpleResponse<List<Long>> upload(@PathVariable String parentType,@PathVariable long parentId,@PathVariable long groupId,
                                              @RequestParam("files") List<MultipartFile> files) {
        return SimpleResponse.ok("ファイルを保存しました。",service.upload(parentType,parentId,groupId,files));
    }
    @PatchMapping("/files/{fileId}")
    public SimpleResponse<Void> renameFile(@PathVariable long fileId,@RequestBody NameRequest request) { service.renameFile(fileId,request.name()); return SimpleResponse.ok("名前を変更しました。",null); }
    @DeleteMapping("/files/{fileId}")
    public SimpleResponse<Void> deleteFile(@PathVariable long fileId) { service.deleteFile(fileId); return SimpleResponse.ok("ファイルを削除しました。",null); }
    @GetMapping("/files/{fileId}/content")
    public ResponseEntity<InputStreamResource> content(@PathVariable long fileId,@RequestParam(defaultValue="inline") String disposition) throws IOException {
        AttachmentFile file=service.findFile(fileId);
        MediaType type=MediaType.parseMediaType(file.mimeType());
        ContentDisposition cd="attachment".equals(disposition)
            ? ContentDisposition.attachment().filename(file.displayName(),StandardCharsets.UTF_8).build()
            : ContentDisposition.inline().filename(file.displayName(),StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(type).contentLength(Files.size(file.path()))
            .header(HttpHeaders.CONTENT_DISPOSITION,cd.toString()).header("X-Content-Type-Options","nosniff")
            .body(new InputStreamResource(Files.newInputStream(file.path())));
    }
}
