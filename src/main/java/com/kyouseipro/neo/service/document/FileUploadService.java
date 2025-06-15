// package com.kyouseipro.neo.service.document;

// import java.io.File;
// import java.io.IOException;
// import java.util.UUID;
// import java.util.regex.Pattern;

// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import com.kyouseipro.neo.component.UploadConfig;
// import com.kyouseipro.neo.entity.data.SimpleData;
// import com.kyouseipro.neo.entity.qualification.QualificationFilesEntity;
// import com.kyouseipro.neo.repository.qualification.QualificationFilesRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class FileUploadService {
//     private final QualificationFilesRepository qualificationFilesRepository;

//     public SimpleData fileUpload(MultipartFile[] files, String folderName, int qualificationsId, String editor) {
//         if (files.length == 0) {
//             SimpleData simpleData = new SimpleData();
//             simpleData.setText("ファイルが空です");
//             return simpleData;
//         }

//         File uploadDir = new File(UploadConfig.getUploadDir() + folderName);
//         if (!uploadDir.exists()) uploadDir.mkdirs();

//         StringBuilder resultStr = new StringBuilder();
//         Pattern invalidChars = Pattern.compile("[/\\\\:*?\"<>`]");

//         for (MultipartFile file : files) {
//             if (!file.isEmpty()) {
//                 try {
//                     String originalFilename = file.getOriginalFilename();
//                     if (originalFilename == null || invalidChars.matcher(originalFilename).find()) {
//                         resultStr.append("無効なファイル名: ").append(originalFilename).append("\n");
//                         continue;
//                     }

//                     originalFilename = originalFilename.replaceAll(" ", "_");
//                     String extension = "";
//                     int dotIndex = originalFilename.lastIndexOf(".");
//                     if (dotIndex != -1) extension = originalFilename.substring(dotIndex);
//                     String safeFilename = UUID.randomUUID().toString() + extension;

//                     File dest = new File(uploadDir, safeFilename);
//                     file.transferTo(dest);

//                     // エンティティ生成
//                     QualificationFilesEntity entity = new QualificationFilesEntity();
//                     entity.setFileName(originalFilename);
//                     entity.setInternalName(safeFilename);
//                     entity.setFolderName(folderName);

//                     qualificationFilesRepository.insertQualificationFiles(entity, editor);
//                     resultStr.append("成功: ").append(safeFilename).append("\n");

//                 } catch (IOException e) {
//                     resultStr.append("失敗: ").append(file.getOriginalFilename()).append(" - ").append(e.getMessage()).append("\n");
//                 }
//             }
//         }

//         SimpleData simpleData = new SimpleData();
//         simpleData.setText(resultStr.toString());
//         return simpleData;
//     }
// }
