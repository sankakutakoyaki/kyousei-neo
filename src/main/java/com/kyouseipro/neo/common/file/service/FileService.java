package com.kyouseipro.neo.common.file.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.file.entity.FileEntity;
import com.kyouseipro.neo.common.file.entity.FileMeta;
import com.kyouseipro.neo.common.file.repository.FileGroupRepository;
import com.kyouseipro.neo.common.file.repository.FileRepository;
import com.kyouseipro.neo.config.UploadConfig;

import lombok.RequiredArgsConstructor;

/**
 * ファイル操作ユーティリティクラス（Java 標準ライブラリのみ使用）。
 * <p>
 * 主な機能:
 * - ファイル名の重複回避
 * - ディレクトリ作成
 * - 拡張子・サイズの検証
 * - ZIPファイルの展開
 * - サムネイル生成（リサイズ）
 * </p>
 */
@Service
@RequiredArgsConstructor
public class FileService {

    // private final AddressRepository addressRepository;
    private final FileGroupService fileGroupService;
    private final FileRepository fileRepository;    
    private final FileGroupRepository fileGroupRepository;

    /** 許可された拡張子のリスト（必要に応じて変更可） */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf", "gif", "zip");

    /** 最大ファイルサイズ（10MB） */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 指定ディレクトリが存在しない場合は作成します。
     *
     * @param directoryPath 作成するディレクトリのパス
     */
    public void ensureDirectoryExists(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 重複を避けたユニークなファイル名を生成します。
     *
     * @param directory        保存先ディレクトリのパス（末尾にスラッシュを含むこと）
     * @param originalFilename 元のファイル名
     * @return 重複を避けたファイル名
     */
    public String getUniqueFilename(String directory, String originalFilename) {
        ensureDirectoryExists(directory);

        File file = new File(directory, originalFilename);
        if (!file.exists()) {
            return originalFilename;
        }

        String name = originalFilename;
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            name = originalFilename.substring(0, dotIndex);
            extension = originalFilename.substring(dotIndex);
        }

        int counter = 1;
        while (file.exists()) {
            String newName = name + "(" + counter + ")" + extension;
            file = new File(directory + newName);
            counter++;
        }

        return file.getName();
    }

    /**
     * 指定されたファイル名の拡張子が許可リストに含まれているかを確認します。
     *
     * @param filename 対象ファイル名
     * @return 許可されていれば true、それ以外は false
     */
    public boolean isAllowedExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) return false;

        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(ext);
    }

    /**
     * ファイルサイズが許容範囲内であるかを確認します。
     *
     * @param fileSize ファイルサイズ（バイト）
     * @return 許容範囲内なら true、それ以外は false
     */
    public boolean isFileSizeAllowed(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }

    /**
     * ZIPファイルを指定ディレクトリに展開します。
     *
     * @param zipFilePath     展開するZIPファイルのパス
     * @param targetDirectory 展開先ディレクトリのパス（末尾にスラッシュが必要）
     * @throws IOException 展開エラー（不正なZIPやアクセス権など）
     */
    public void extractZipFile(String zipFilePath, String targetDirectory) throws IOException {
        ensureDirectoryExists(targetDirectory);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(targetDirectory, entry.getName());

                // ディレクトリならスキップ
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                    continue;
                }

                // パストラバーサル防止
                String canonicalDirPath = new File(targetDirectory).getCanonicalPath();
                String canonicalFilePath = newFile.getCanonicalPath();
                if (!canonicalFilePath.startsWith(canonicalDirPath)) {
                    throw new IOException("不正なZIPエントリ: " + entry.getName());
                }

                // 親ディレクトリ作成
                new File(newFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
    }

    /**
     * 画像ファイルをリサイズしてサムネイルを作成します。
     *
     * @param inputImagePath  元画像のファイルパス
     * @param outputImagePath サムネイル画像の保存パス（拡張子に応じて保存形式が決定）
     * @param width           リサイズ後の幅（ピクセル）
     * @param height          リサイズ後の高さ（ピクセル）
     * @throws IOException 入出力エラーや画像処理エラー
     */
    public void createThumbnail(String inputImagePath, String outputImagePath, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(inputImagePath));
        if (originalImage == null) {
            throw new IOException("画像を読み込めませんでした: " + inputImagePath);
        }

        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        String ext = getExtension(outputImagePath);
        ImageIO.write(resizedImage, ext, new File(outputImagePath));
    }

    /**
     * ファイル名から拡張子を取得します。
     *
     * @param filename 対象のファイル名
     * @return 拡張子（例: jpg, png）
     */
    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot != -1) ? filename.substring(dot + 1).toLowerCase() : "";
    }

    /**
     * ファイルをアップロードする
     * @param files
     * @param folderName
     * @param entity
     * @return
     */
    // public List<FileUpload> fileUpload(MultipartFile[] files, String folderName, FileUpload fileUploadEntity) {
    //     if (files.length == 0) {
    //         return null;
    //     }

    //     File uploadDir = new File(UploadConfig.getUploadDir() + folderName);
    //     if (!uploadDir.exists()) {
    //         uploadDir.mkdirs();
    //     }

    //     List<FileUpload> list = new ArrayList<>();
    //     Pattern invalidChars = Pattern.compile("[/\\\\:*?\"<>`]");

    //     for (MultipartFile file : files) {
    //         if (!file.isEmpty()) {
    //             try {
    //                 String originalFilename = file.getOriginalFilename();
    //                 if (originalFilename == null || invalidChars.matcher(originalFilename).find()) {
    //                     continue;
    //                 }

    //                 originalFilename = originalFilename.replaceAll(" ", "_");

    //                 String extension = "";
    //                 int dotIndex = originalFilename.lastIndexOf(".");
    //                 if (dotIndex != -1) {
    //                     extension = originalFilename.substring(dotIndex);
    //                 }

    //                 String safeFilename = UUID.randomUUID().toString() + extension;
    //                 File dest = new File(uploadDir, safeFilename);
    //                 file.transferTo(dest);

    //                 FileUpload entity = fileUploadEntity.create();
    //                 // エンティティに設定
    //                 entity.setFileName(originalFilename);
    //                 entity.setInternalName(safeFilename);
    //                 entity.setFolderName(dest.getAbsolutePath());
    //                 list.add(entity);
    //             } catch (IOException e) {
    //                 continue;
    //             }
    //         }
    //     }

    //     return list;
    // }
    // public List<FileMeta> saveFiles(
    //         MultipartFile[] files,
    //         String folderName) {

    //     if (files == null || files.length == 0) {
    //         return Collections.emptyList();
    //     }

    //     File uploadDir = new File(UploadConfig.getUploadDir() + folderName);
    //     if (!uploadDir.exists()) {
    //         uploadDir.mkdirs();
    //     }

    //     List<FileMeta> list = new ArrayList<>();

    //     for (MultipartFile file : files) {

    //         if (file.isEmpty()) continue;

    //         try {
    //             String original = file.getOriginalFilename();
    //             if (original == null) continue;

    //             original = original.replaceAll(" ", "_");

    //             String ext = "";
    //             int dot = original.lastIndexOf(".");
    //             if (dot != -1) ext = original.substring(dot);

    //             String stored = UUID.randomUUID() + ext;

    //             File dest = new File(uploadDir, stored);
    //             file.transferTo(dest);

    //             FileMeta meta = new FileMeta();
    //             meta.setStoredName(stored);
    //             meta.setOriginalName(original);
    //             meta.setMimeType(file.getContentType());
    //             meta.setFileSize(file.getSize());

    //             if (meta.getMimeType() != null &&
    //                 meta.getMimeType().startsWith("image/")) {

    //                 BufferedImage img = ImageIO.read(dest);
    //                 if (img != null) {
    //                     meta.setWidth(img.getWidth());
    //                     meta.setHeight(img.getHeight());
    //                 }
    //             }

    //             list.add(meta);

    //         } catch (IOException ignored) {}
    //     }

    //     return list;
    // }

    /**
     * ファイル削除用エンドポイント
     * @param url 削除するファイルのパス
     * @return
     */
    // public boolean deleteFile(String url) {
    //     try {
    //         Path filePath = Paths.get(url);
    //         return Files.deleteIfExists(filePath);
    //     } catch (IOException e) {
    //         return false;
    //     }
    // }
    @Transactional
    public boolean deleteFile(Long fileId) {

        // ① 取得（ACTIVEのみ）
        FileEntity file = fileRepository.findActiveById(fileId);
        if (file == null) {
            throw new RuntimeException("File not found");
        }

        Long groupId = file.getGroupId();

        // ② 物理削除
        Path path = Paths.get(
                UploadConfig.getUploadDir(),
                file.getParentType(),
                file.getParentId().toString(),
                file.getStoredName()
        );

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Physical delete failed", e);
        }

        // ③ file 論理削除
        fileRepository.updateState(fileId, Enums.state.DELETE.getCode());

        // ④ group内ACTIVE件数確認
        int activeCount =
                fileRepository.countByGroupIdAndState(
                        groupId,
                        Enums.state.INITIAL.getCode()
                );

        // ⑤ 0ならgroupも論理削除
        if (activeCount == 0) {
            fileGroupRepository.updateState(
                    groupId,
                    Enums.state.DELETE.getCode()
            );
            return true; // group deleted
        }

        return false;
    }

    public String toCsvField(Object value) {
        if (value == null) {
            return ",";
        }
        String str = String.valueOf(value);
        boolean needsQuote = str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r");
        if (needsQuote) {
            str = str.replace("\"", "\"\""); // ダブルクォートのエスケープ
            return "\"" + str + "\",";
        } else {
            return str + ",";
        }
    }

    // public AddressEntity getAddressByPostalCode(String postalCode) {
    //     return addressRepository.findByPostalCode(postalCode);
    // }


    @Transactional
    public List<Long> uploadFiles(
            String parentType,
            Long parentId,
            Long groupId,
            MultipartFile[] files
    ) throws IOException {

        parentType = parentType.toLowerCase();

        if (groupId == null || !fileGroupRepository.exists(groupId)) {
            groupId = fileGroupService.createGroup(parentType, parentId, "自動作成グループ");
        }

        List<Long> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {

            if (file.isEmpty()) continue;

            String originalName = file.getOriginalFilename();
            if (originalName == null) continue;

            originalName = originalName.replaceAll(" ", "_");

            // 拡張子取得
            String ext = "";
            int dot = originalName.lastIndexOf(".");
            if (dot != -1) {
                ext = originalName.substring(dot);
            }

            // 保存用ファイル名
            String storedName = UUID.randomUUID() + ext;

            // 保存先パス
            Path savePath = Paths.get(
                    UploadConfig.getUploadDir(),
                    parentType,
                    parentId.toString(),
                    storedName
            );

            // ディレクトリ作成
            Files.createDirectories(savePath.getParent());

            // 物理保存
            file.transferTo(savePath.toFile());

            // DB保存
            FileEntity entity = new FileEntity();
            entity.setParentType(parentType);
            entity.setParentId(parentId);
            entity.setGroupId(groupId);
            entity.setOriginalName(originalName);
            entity.setStoredName(storedName);
            entity.setDisplayName(originalName);
            entity.setMimeType(file.getContentType());
            entity.setFileSize(file.getSize());
            entity.setFileType(detectFileType(file.getContentType()));
            entity.setDisplayOrder(
                    fileRepository.getNextDisplayOrder(groupId)
            );

            Long fileId = fileRepository.insert(entity);
            fileIds.add(fileId);
        }

        return fileIds;
    }

    private String detectFileType(String mimeType) {
        if (mimeType == null) return "OTHER";
        if (mimeType.startsWith("image")) return "IMAGE";
        if (mimeType.equals("application/pdf")) return "PDF";
        return "OTHER";
    }

    // @Transactional
    // public void deleteFile(Long fileId) {

    //     FileEntity file = fileRepository.findById(fileId);

    //     if (file == null) {
    //         throw new RuntimeException("ファイルが存在しません");
    //     }

    //     Long groupId = file.getGroupId();
    //     int deletedOrder = file.getDisplayOrder();

    //     // ① 削除
    //     fileRepository.delete(fileId);

    //     // ② display_order 詰める
    //     fileRepository.decrementDisplayOrderAfter(
    //             groupId, deletedOrder
    //     );
    // }

    /**
     * display_name 重複チェック（DB）
     * @param groupId
     * @param originalName
     * @return
     */
    public String createUniqueDisplayName(
            Long groupId,
            Long fileId,
            String originalName) {

        String baseName = originalName;
        String extension = "";

        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        int counter = 0;
        String candidate;

        do {
            if (counter == 0) {
                candidate = baseName + extension;
            } else {
                candidate = baseName + "(" + counter + ")" + extension;
            }
            counter++;
        } while (fileRepository.existsDisplayName(groupId, fileId, candidate));

        return candidate;
    }

    @Transactional
    public void renameFile(Long fileId, String newName) {
        if (newName == null || newName.isEmpty()) return;

        // 重複回避
        FileEntity entity = fileRepository.findById(fileId);
        String uniqueName = createUniqueDisplayName(entity.getGroupId(), newName);

        entity.setDisplayName(uniqueName);
        fileRepository.updateDisplayName(fileId, uniqueName);
    }

    /** groupId内でのユニーク表示名を作成 */
    public String createUniqueDisplayName(Long groupId, String originalName) {
        String baseName = originalName;
        String extension = "";

        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        int counter = 0;
        String candidate;

        do {
            candidate = counter == 0 ? baseName + extension : baseName + "(" + counter + ")" + extension;
            counter++;
        } while (fileRepository.existsDisplayName(groupId, candidate));

        return candidate;
    }
}
