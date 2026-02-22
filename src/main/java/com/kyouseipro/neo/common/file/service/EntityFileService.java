// package com.kyouseipro.neo.common.file.service;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.kyouseipro.neo.common.file.entity.FileEntity;
// import com.kyouseipro.neo.common.file.repository.EntityFileRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class EntityFileService {

//     private final EntityFileRepository repository;
//     private final Path root = Paths.get("uploads");

//     // 一覧
//     public List<FileEntity> list(String entity, Long id) {
//         return repository.find(entity, id);
//     }

//     // ★ 追加：ID取得用
//     public FileEntity findById(Long fileId) {
//         return repository.findById(fileId);
//     }

//     // ★ 修正版：group付き
//     public FileEntity upload(
//             String entity,
//             Long id,
//             String group,
//             MultipartFile file) throws IOException {

//         String folder = entity + "/" + id + "/" + group;
//         Path dir = root.resolve(folder);
//         Files.createDirectories(dir);

//         String internal =
//                 UUID.randomUUID() + "_" + file.getOriginalFilename();

//         Path savePath = dir.resolve(internal);

//         Files.copy(file.getInputStream(),
//                 savePath,
//                 StandardCopyOption.REPLACE_EXISTING);

//         FileEntity f = new FileEntity();
//         f.setEntityName(entity);
//         f.setEntityId(id);
//         f.setFileGroup(group);
//         f.setFileName(file.getOriginalFilename());
//         f.setInternalName(internal);
//         f.setFolderName(folder);
//         f.setFileSize(file.getSize());
//         f.setContentType(file.getContentType());

//         Long newId = repository.insert(f);
//         f.setEntityFileId(newId);

//         return f;
//     }

//     public void delete(Long fileId) {
//         repository.logicalDelete(fileId);
//     }
// }