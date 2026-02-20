package com.kyouseipro.neo.common.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;
import com.kyouseipro.neo.common.file.entity.ConstructionFileGroupEntity;
import com.kyouseipro.neo.common.file.repository.ConstructionFileGroupRepository;
import com.kyouseipro.neo.common.file.repository.ConstructionFileRepository;
import com.kyouseipro.neo.config.UploadConfig;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class ConstructionFileService {

    private final ConstructionFileRepository fileRepository;
    private final ConstructionFileGroupRepository groupRepository;

    // 実際はapplication.ymlから取得推奨
    private static final String BASE_PATH = "D:/files/";

    /**
     * ファイルアップロード（複数対応）
     */
    public void uploadFiles(
            Long constructionId,
            Long groupId,
            List<MultipartFile> files
    ) throws IOException {

        Path dir = Paths.get(BASE_PATH, String.valueOf(constructionId));
        Files.createDirectories(dir);

        for (MultipartFile multipart : files) {

            if (multipart.isEmpty()) continue;

            String mime = multipart.getContentType();
            String originalName = multipart.getOriginalFilename();
            String extension = getExtension(originalName);

            // UUID生成
            String uuid = UUID.randomUUID().toString();
            String storedName = uuid + extension;

            Path savePath = dir.resolve(storedName);

            // 物理保存
            Files.copy(multipart.getInputStream(), savePath,
                    StandardCopyOption.REPLACE_EXISTING);

            // DB登録用Entity作成
            ConstructionFileEntity file = new ConstructionFileEntity();
            file.setConstructionId(constructionId);
            file.setGroupId(groupId);
            file.setStoredName(storedName);
            file.setOriginalName(originalName);
            file.setDisplayName(originalName);
            file.setMimeType(mime);
            file.setFileSize(multipart.getSize());
            file.setDisplayOrder(0);

            // MIME判定
            if (mime != null && mime.startsWith("image/")) {
                file.setFileType("IMAGE");
                setImageSize(file, savePath);
            } else if ("application/pdf".equals(mime)) {
                file.setFileType("PDF");
            } else {
                file.setFileType("OTHER");
            }

            fileRepository.insert(file);
        }
    }

    /**
     * 画像サイズ取得
     */
    private void setImageSize(ConstructionFileEntity file, Path path) {

        try {
            BufferedImage img = ImageIO.read(path.toFile());
            if (img != null) {
                file.setWidth(img.getWidth());
                file.setHeight(img.getHeight());
            }
        } catch (IOException e) {
            // ログ出力推奨
        }
    }

    /**
     * 拡張子取得
     */
    private String getExtension(String filename) {

        if (filename == null) return "";

        int index = filename.lastIndexOf(".");
        if (index < 0) return "";

        return filename.substring(index);
    }

    /**
     * ファイル削除（物理＋DB）
     */
    public void deleteFile(Long constructionId, ConstructionFileEntity file) throws IOException {

        Path path = Paths.get(BASE_PATH,
                String.valueOf(constructionId),
                file.getStoredName());

        Files.deleteIfExists(path);

        fileRepository.delete(file.getFileId());
    }

    /**
     * 画面表示用に「グループ＋配下ファイル」をまとめる
     */
    public List<ConstructionFileGroupEntity> getGroupsWithFiles(Long constructionId) {

        List<ConstructionFileGroupEntity> groups =
                groupRepository.findByConstructionId(constructionId);

        for (ConstructionFileGroupEntity group : groups) {
            group.setFiles(
                fileRepository.findByGroupId(group.getGroupId())
            );
        }

        return groups;
    }

    // private final ConstructionFileRepository repository;

    // private final String basePath = "C:/files/";

    public List<ConstructionFileEntity> saveFiles(Long groupId, MultipartFile[] files) throws IOException {

        List<ConstructionFileEntity> savedFiles = new ArrayList<>();

        Path groupPath = Paths.get(UploadConfig.getUploadDir() + groupId);
        // Path groupPath = Paths.get(basePath + groupId);
        
        Files.createDirectories(groupPath);

        for (MultipartFile file : files) {

            String uuid = UUID.randomUUID().toString();
            String ext = getExtension(file.getOriginalFilename());
            String storedName = uuid + "." + ext;

            Path savePath = groupPath.resolve(storedName);
            file.transferTo(savePath);

            ConstructionFileEntity entity = new ConstructionFileEntity();
            entity.setGroupId(groupId);
            entity.setOriginalName(file.getOriginalFilename());
            entity.setStoredName(storedName);
            entity.setFileType(file.getContentType());
            entity.setFileSize(file.getSize());
            entity.setCreateDate(LocalDateTime.now());

            fileRepository.save(entity);

            savedFiles.add(entity);
        }

        return savedFiles;
    }

    // private String getExtension(String filename) {
    //     return filename.substring(filename.lastIndexOf(".") + 1);
    // }
}