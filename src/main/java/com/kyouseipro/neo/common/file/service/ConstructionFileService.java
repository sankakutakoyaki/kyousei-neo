package com.kyouseipro.neo.common.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;
import com.kyouseipro.neo.common.file.repository.ConstructionFileGroupRepository;
import com.kyouseipro.neo.common.file.repository.ConstructionFileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConstructionFileService {
    
    private final ConstructionFileRepository fileRepository;
    private final ConstructionFileGroupRepository groupRepository;
    private final ConstructionFileGroupService constructionFileGroupService;

    @Transactional
    public List<Long> uploadFiles(
            Long constructionId,
            Long groupId,
            MultipartFile[] files
    ) throws IOException {

        // ① groupが無い場合は作成
        if (groupId == null || !groupRepository.exists(groupId)) {
            // groupId = groupRepository.insert(constructionId);
            constructionFileGroupService.createGroup(constructionId, "自動作成グループ");
        }

        List<Long> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {

            String originalName = file.getOriginalFilename();
            // ★ ここで重複回避
            String displayName = createUniqueDisplayName(groupId, null, originalName);

            ConstructionFileEntity entity = new ConstructionFileEntity();
            entity.setConstructionId(constructionId);
            entity.setGroupId(groupId);
            entity.setOriginalName(originalName);
            entity.setStoredName(UUID.randomUUID().toString());
            entity.setDisplayName(displayName);
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

    @Transactional
    public void deleteFile(Long fileId) {

        ConstructionFileEntity file = fileRepository.findById(fileId);

        if (file == null) {
            throw new RuntimeException("ファイルが存在しません");
        }

        Long groupId = file.getGroupId();
        int deletedOrder = file.getDisplayOrder();

        // ① 削除
        fileRepository.delete(fileId);

        // ② display_order 詰める
        fileRepository.decrementDisplayOrderAfter(
                groupId, deletedOrder
        );
    }

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

    // @Transactional
    // public void rename(Long fileId, String newName) {

    //     ConstructionFileEntity file =
    //             fileRepository.findById(fileId);

    //     Long groupId = file.getGroupId();

    //     String uniqueName =
    //             createUniqueDisplayName(groupId, fileId, newName);

    //     fileRepository.updateDisplayName(fileId, uniqueName);
    // }
    @Transactional
    public void renameFile(Long fileId, String newName) {
        if (newName == null || newName.isEmpty()) return;

        // 重複回避
        ConstructionFileEntity entity = fileRepository.findById(fileId);
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