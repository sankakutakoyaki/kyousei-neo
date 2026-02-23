package com.kyouseipro.neo.common.file.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.file.entity.FileDto;
import com.kyouseipro.neo.common.file.entity.FileEntity;
import com.kyouseipro.neo.common.file.entity.GroupRequest;
import com.kyouseipro.neo.common.file.repository.FileRepository;
import com.kyouseipro.neo.common.file.service.FileGroupService;
import com.kyouseipro.neo.common.file.service.FileService;
import com.kyouseipro.neo.config.UploadConfig;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final FileGroupService fileGroupService;
    private final FileRepository fileRepository;

    @PostMapping("/file/upload/{parentType}/{parentId}")
    // @ResponseBody
    public List<Long> upload(
            @PathVariable String parentType,
            @PathVariable Long parentId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "groupId", required = false) Long groupId
    ) throws IOException {

        return fileService.uploadFiles(parentType.toUpperCase(), parentId, groupId, files);
    }

    @GetMapping("/{parentType}/{parentId}")
    @ResponseBody
    public List<FileDto> list(@PathVariable String parentType, @PathVariable Long parentId) {
        
        return fileRepository.findFiles(parentType.toUpperCase(), parentId);
    }

    /** 削除 */
    @PostMapping("/file/delete/{fileId}")
    @ResponseBody
    public void deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
    }

    /** ファイル名変更 */
    @PostMapping("/file/rename/{fileId}")
    @ResponseBody
    public void renameFile(
            @PathVariable Long fileId,
            @RequestBody Map<String, String> body
    ) {
        String newName = body.get("displayName");
        fileService.renameFile(fileId, newName);
    }

    /** グループ名変更 */
    @PostMapping("/group/rename/{groupId}")
    @ResponseBody
    public void renameGroup(
            @PathVariable Long groupId,
            @RequestBody Map<String, String> body
    ) {
        String newName = body.get("groupName");
        fileGroupService.renameGroup(groupId, newName);
    }

    @PostMapping("/{parentType}/{parentId}/group")
    @ResponseBody
    public Long createGroup(
            @PathVariable String parentType,
            @PathVariable Long parentId,
            @RequestBody GroupRequest request) {

        return fileGroupService.createGroup(parentType, parentId, request.getGroupName());
    }

    /**
     * ファイルを別タブで開く
     * @param category
     * @param folder
     * @param filename
     * @return
     * @throws IOException
     */
    @GetMapping("/{category}/{folder}/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String category, @PathVariable String folder, @PathVariable String filename) throws IOException {
        // 実際の保存ディレクトリ（必要に応じて調整）
        Path filePath = Paths.get(UploadConfig.getUploadDir(), category, folder, filename);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }

        UrlResource resource = new UrlResource(filePath.toUri());

        // MIMEタイプの判定（PDFなど）
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
            .orElse(MediaType.APPLICATION_OCTET_STREAM);

        // Content-Disposition ヘッダー（inline 表示用）
        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8).replace("+", "%20") + "\"")
            .body(resource);
    }

    @GetMapping("/api/files")
    public List<FileEntity> getFiles(
            @RequestParam String parentType,
            @RequestParam Long parentId) {

        return fileRepository.findByParent(parentType, parentId);
    }

    @GetMapping("/{internalName}")
    public ResponseEntity<Resource> getFile(@PathVariable String internalName) throws Exception {

        Path path = Paths.get("upload-dir").resolve(internalName);
        Resource resource = new UrlResource(path.toUri());

        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }











    // /**
    //  * IDから資格のファイルを取得する
    //  * @param ID
    //  * @return 
    //  */
    // @PostMapping("/get/qualifications")
	// @ResponseBody
    // public List<QualificationFilesEntity> getQualificationsFilesById(@RequestParam Integer id, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     return qualificationFilesService.getQualificationFilesById(id, userName);
    // }

    // /**
    //  * URLから資格のファイルを削除する
    //  * @param ID
    //  * @return 
    //  */
    // @PostMapping("/delete/qualifications")
	// @ResponseBody
    // public ResponseEntity<SimpleResponse<Integer>> deleteQualificationsFilesByUrl(@RequestParam String url, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     String uploadStr = UploadConfig.getUploadDir() + "qualification/" + url;
    //     boolean result = fileService.deleteFile(uploadStr);
    //     if (result) {
    //         Integer resultInt = qualificationFilesService.deleteQualificationsFilesByUrl(uploadStr, userName);
    //         if (resultInt != null && resultInt > 0) {
    //             return ResponseEntity.ok(SimpleResponse.ok("削除しました。", resultInt));
    //         }
    //     }
    //     return ResponseEntity.badRequest().body(SimpleResponse.error("削除に失敗しました"));
    // }

    // /**
    //  * 資格情報のPDFファイルをアップロードする
    //  * @param files
    //  * @param folderName
    //  * @param id
    //  * @return
    //  */
    // @PostMapping("/upload/")
    // @ResponseBody
    // public List<QualificationFilesEntity> fileUpload(@RequestParam("files") MultipartFile[] files, @RequestParam("folder_name") String folderName, @RequestParam("id") int id, @AuthenticationPrincipal OidcUser principal) {
    //     String userName = principal.getAttribute("preferred_username");
    //     QualificationFilesEntity entity = new QualificationFilesEntity();
    //     entity.setQualificationsId(id);

    //     List<FileUpload> entities = fileService.fileUpload(files, folderName, entity);
    //     qualificationFilesService.saveQualificationsFiles(entities, userName, id);
    //     return qualificationFilesService.getQualificationFilesById(id, userName);
    // }

    // /**
    //  * 郵便番号から住所を取得する
    //  * @param postal_code
    //  * @return AddressEntity
    //  */
    // @PostMapping("/address/get/postalcode")
	// @ResponseBody
    // public ResponseEntity getAddressFromPostalCode(@RequestParam String postal_code) {
    //     AddressEntity entity = fileService.getAddressByPostalCode(postal_code);
    //     return ResponseEntity.ok(entity);
    // }
}
