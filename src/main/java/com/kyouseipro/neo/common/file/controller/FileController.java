package com.kyouseipro.neo.common.file.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.address.entity.AddressEntity;
import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;
import com.kyouseipro.neo.common.file.service.FileService;
import com.kyouseipro.neo.common.response.SimpleResponse;
import com.kyouseipro.neo.config.UploadConfig;
import com.kyouseipro.neo.interfaces.FileUpload;
import com.kyouseipro.neo.qualification.entity.QualificationFilesEntity;
import com.kyouseipro.neo.qualification.service.QualificationFilesService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

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

    /**
     * ファイルを別タブで開く
     * @param category
     * @param folder
     * @param filename
     * @return
     * @throws IOException
     */
    @GetMapping("/{category}/{folder}/{filename}")
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
}
