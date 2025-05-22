package com.kyouseipro.neo.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.component.UploadConfig;
import com.kyouseipro.neo.entity.data.SqlData;
import com.kyouseipro.neo.entity.master.AddressEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FileController {
    private final SqlRepository sqlRepository;
    /**
     * 郵便番号から住所を取得する
     * @param postal_code
     * @return AddressEntity
     */
    @PostMapping("/address/get/postalcode")
	@ResponseBody
    public IEntity getAddressFromPostalCode(@RequestParam String postal_code) {
        StringBuilder sb = new StringBuilder("SELECT * From address WHERE postal_code = '" + postal_code + "';");
        SqlData sqlData = new SqlData();
        sqlData.setData(sb.toString(), new AddressEntity());
        return sqlRepository.getEntity(sqlData);
    }

    /**
     * ファイルを別タブで開く
     * @param category
     * @param folder
     * @param filename
     * @return
     * @throws IOException
     */
    @GetMapping("/files/{category}/{folder}/{filename}")
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
}
