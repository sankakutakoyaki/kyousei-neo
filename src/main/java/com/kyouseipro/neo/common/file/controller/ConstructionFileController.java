// package com.kyouseipro.neo.common.file.controller;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import com.kyouseipro.neo.common.file.entity.ConstructionFileDto;
// import com.kyouseipro.neo.common.file.entity.GroupRequest;
// import com.kyouseipro.neo.common.file.repository.ConstructionFileRepository;
// import com.kyouseipro.neo.common.file.service.ConstructionFileGroupService;
// import com.kyouseipro.neo.common.file.service.ConstructionFileService;

// import java.io.IOException;
// import java.util.List;
// import java.util.Map;

// @Controller
// @RequiredArgsConstructor
// @RequestMapping("/api/construction")
// public class ConstructionFileController {

//     private final ConstructionFileService constructionFileService;
//     private final ConstructionFileGroupService constructionFileGroupService;
//     private final ConstructionFileRepository constructionFileRepository;

//     @PostMapping("/upload")
//     @ResponseBody
//     public List<Long> upload(
//             @RequestParam("files") MultipartFile[] files,
//             @RequestParam("constructionId") Long constructionId,
//             @RequestParam(value = "groupId", required = false) Long groupId
//     ) throws IOException {

//         return constructionFileService.uploadFiles(constructionId, groupId, files);
//     }

//     @GetMapping("/{constructionId}/files")
//     @ResponseBody
//     public List<ConstructionFileDto> list(@PathVariable Long constructionId) {
//         return constructionFileRepository.findFiles(constructionId);
//     }

//     @PostMapping("/file/{fileId}")
//     @ResponseBody
//     public void deleteFile(@PathVariable Long fileId) {
//         constructionFileService.deleteFile(fileId);
//     }

//     /** ファイル名変更 */
//     @PostMapping("/file/{fileId}/rename")
//     public void renameFile(
//             @PathVariable Long fileId,
//             @RequestBody Map<String, String> body
//     ) {
//         String newName = body.get("displayName");
//         constructionFileService.renameFile(fileId, newName);
//     }

//     /** グループ名変更 */
//     @PostMapping("/group/{groupId}/rename")
//     public void renameGroup(
//             @PathVariable Long groupId,
//             @RequestBody Map<String, String> body
//     ) {
//         String newName = body.get("groupName");
//         constructionFileGroupService.renameGroup(groupId, newName);
//     }

//     @PostMapping("/{constructionId}/group")
//     @ResponseBody
//     public Long createGroup(
//             @PathVariable Long constructionId,
//             @RequestBody GroupRequest request) {

//         return constructionFileGroupService.createGroup(constructionId, request.getGroupName());
//     }
//     // /**
//     //  * 施工写真一覧表示
//     //  */
//     // @GetMapping("/{constructionId}/files")
//     // public String view(
//     //         @PathVariable Long constructionId,
//     //         Model model
//     // ) {

//     //     List<ConstructionFileGroupEntity> groups =
//     //             groupRepository.findByConstructionId(constructionId);

//     //     for (ConstructionFileGroupEntity group : groups) {

//     //         List<ConstructionFileEntity> files =
//     //                 fileRepository.findByGroupId(group.getGroupId());

//     //         group.setFiles(files);   // DTOにList追加しておく
//     //     }

//     //     model.addAttribute("constructionId", constructionId);
//     //     // model.addAttribute("groups", groups);
//     //     model.addAttribute("groups", fileService.getGroupsWithFiles(constructionId));

//     //     return "construction/files";   // thymeleaf
//     // }
// }