package com.kyouseipro.neo.domain.attachment.application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.common.exception.SystemException;
import com.kyouseipro.neo.config.UploadConfig;
import com.kyouseipro.neo.domain.attachment.model.AttachmentFile;
import com.kyouseipro.neo.domain.attachment.model.AttachmentGroup;
import com.kyouseipro.neo.domain.attachment.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif");
    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);
    private final UploadConfig uploadConfig;
    private final AttachmentRepository repository;

    public List<AttachmentGroup> findGroups(String parentType, long parentId) {
        return repository.findGroups(normalizeParentType(parentType), positive(parentId, "親データ"));
    }

    public long createGroup(String parentType, long parentId, String name) {
        return repository.insertGroup(normalizeParentType(parentType), positive(parentId, "親データ"), requiredName(name, "フォルダ名", 100));
    }

    public void renameGroup(long id, String name) { repository.renameGroup(positive(id,"フォルダ"), requiredName(name,"フォルダ名",100)); }
    public void renameFile(long id, String name) { repository.renameFile(positive(id,"ファイル"), requiredName(name,"ファイル名",255)); }

    public List<Long> upload(String parentType, long parentId, long groupId, List<MultipartFile> files) {
        String type = normalizeParentType(parentType);
        positive(parentId,"親データ"); positive(groupId,"フォルダ");
        if (!repository.groupBelongsTo(groupId,type,parentId)) throw new BusinessException("添付先フォルダが見つかりません。");
        if (files == null || files.isEmpty()) throw new BusinessException("ファイルを選択してください。");
        return files.stream().map(file -> saveOne(groupId,file)).toList();
    }

    public AttachmentFile findFile(long id) {
        AttachmentFile file = repository.findFile(positive(id,"ファイル"));
        if (file == null) throw new BusinessException("ファイルが見つかりません。");
        Path root = attachmentRoot();
        Path path = root.resolve(file.path()).normalize();
        if (!path.startsWith(root) || !Files.isRegularFile(path)) throw new BusinessException("ファイルが見つかりません。");
        return new AttachmentFile(file.displayName(),file.mimeType(),path);
    }

    @Transactional
    public void deleteFile(long id) {
        AttachmentFile file = findFile(id);
        repository.deleteFile(id);
        deleteQuietly(file.path());
    }

    @Transactional
    public void deleteGroup(long id) {
        positive(id,"フォルダ");
        List<AttachmentFile> files = repository.findFilesInGroup(id);
        repository.deleteGroup(id);
        files.forEach(file -> deleteQuietly(attachmentRoot().resolve(file.path()).normalize()));
        try { Files.deleteIfExists(attachmentRoot().resolve(Long.toString(id))); } catch (IOException ignored) {}
    }

    private long saveOne(long groupId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException("空のファイルは保存できません。");
        String mime = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String fileType;
        Integer width = null, height = null;
        if (IMAGE_TYPES.contains(mime)) {
            fileType = "IMAGE";
            try (InputStream in = file.getInputStream()) {
                BufferedImage image = ImageIO.read(in);
                if (image == null) throw new BusinessException("対応していない画像形式です。JPEG、PNG、GIFを選択してください。");
                width = image.getWidth(); height = image.getHeight();
            } catch (IOException e) { throw new SystemException("画像を確認できませんでした。",e); }
        } else if ("application/pdf".equals(mime)) {
            fileType = "PDF";
            try (InputStream in = file.getInputStream()) {
                if (!Arrays.equals(PDF_SIGNATURE,in.readNBytes(PDF_SIGNATURE.length))) throw new BusinessException("正しいPDFファイルではありません。");
            } catch (IOException e) { throw new SystemException("PDFを確認できませんでした。",e); }
        } else {
            throw new BusinessException("保存できる形式はJPEG、PNG、GIF、PDFです。");
        }
        String original = safeOriginalName(file.getOriginalFilename());
        String extension = extensionFor(mime);
        String stored = UUID.randomUUID() + extension;
        Path directory = attachmentRoot().resolve(Long.toString(groupId));
        Path destination = directory.resolve(stored).normalize();
        try {
            Files.createDirectories(directory);
            try (InputStream in = file.getInputStream()) { Files.copy(in,destination,StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException e) { throw new SystemException("添付ファイルの保存に失敗しました。",e); }
        try { return repository.insertFile(groupId,stored,original,fileType,mime,file.getSize(),width,height); }
        catch (RuntimeException e) { deleteQuietly(destination); throw e; }
    }

    private Path attachmentRoot() { return uploadConfig.getUploadDirectory().resolve("attachments").normalize(); }
    private String normalizeParentType(String value) {
        String type = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if (!type.matches("[A-Z][A-Z0-9_]{0,39}")) throw new BusinessException("添付先の種類が不正です。");
        return type;
    }
    private long positive(long value,String label) { if(value<=0) throw new BusinessException(label+"の指定が不正です。"); return value; }
    private String requiredName(String value,String label,int max) {
        String name=value==null?"":value.trim();
        if(name.isEmpty()) throw new BusinessException(label+"を入力してください。");
        if(name.length()>max) throw new BusinessException(label+"は"+max+"文字以内で入力してください。");
        return name;
    }
    private String safeOriginalName(String value) {
        String name=value==null?"file":Path.of(value).getFileName().toString().trim();
        return requiredName(name,"ファイル名",255);
    }
    private String extensionFor(String mime) { return switch(mime){case "image/jpeg"->".jpg";case "image/png"->".png";case "image/gif"->".gif";default->".pdf";}; }
    private void deleteQuietly(Path path) { try { Files.deleteIfExists(path); } catch(IOException ignored) {} }
}
