package com.kyouseipro.neo.service.dto;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.component.UploadConfig;
import com.kyouseipro.neo.entity.common.AddressEntity;
import com.kyouseipro.neo.interfaceis.FileUpload;
import com.kyouseipro.neo.repository.common.AddressRepository;

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

    private final AddressRepository addressRepository;

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

        File file = new File(directory + originalFilename);
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
    public List<FileUpload> fileUpload(MultipartFile[] files, String folderName, FileUpload fileUploadEntity) {
        if (files.length == 0) {
            return null;
        }

        File uploadDir = new File(UploadConfig.getUploadDir() + folderName);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        List<FileUpload> list = new ArrayList<>();
        Pattern invalidChars = Pattern.compile("[/\\\\:*?\"<>`]");

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String originalFilename = file.getOriginalFilename();
                    if (originalFilename == null || invalidChars.matcher(originalFilename).find()) {
                        continue;
                    }

                    originalFilename = originalFilename.replaceAll(" ", "_");

                    String extension = "";
                    int dotIndex = originalFilename.lastIndexOf(".");
                    if (dotIndex != -1) {
                        extension = originalFilename.substring(dotIndex);
                    }

                    String safeFilename = UUID.randomUUID().toString() + extension;
                    File dest = new File(uploadDir, safeFilename);
                    file.transferTo(dest);

                    FileUpload entity = fileUploadEntity.create();
                    // エンティティに設定
                    entity.setFileName(originalFilename);
                    entity.setInternalName(safeFilename);
                    entity.setFolderName(dest.getAbsolutePath());
                    list.add(entity);
                } catch (IOException e) {
                    continue;
                }
            }
        }

        return list;
    }

    /**
     * ファイル削除用エンドポイント
     * @param url 削除するファイルのパス
     * @return
     */
    public boolean deleteFile(String url) {
        try {
            Path filePath = Paths.get(url);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
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

    public Optional<AddressEntity> getAddressByPostalCode(String postalCode) {
        return addressRepository.findByPostalCode(postalCode);
    }
}
