package com.kyouseipro.neo.common.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kyouseipro.neo.common.file.entity.ConstructionFileEntity;
import com.kyouseipro.neo.common.file.service.ConstructionFileService;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
// @RequestMapping("/api/construction")
public class ConstructionFileController {

    // private final ConstructionFileGroupRepository groupRepository;
    // private final ConstructionFileRepository fileRepository;
    private final ConstructionFileService fileService;


    /**
     * 
     */
    // @PostMapping("/upload/{groupId}")
    // public List<ConstructionFileEntity> upload(
    //         @PathVariable Long groupId,
    //         @RequestParam("files") MultipartFile[] files) throws IOException {
    //     System.out.println("test");
    //     return service.saveFiles(groupId, files);
    // }
    @PostMapping("/api/construction/upload")
    @ResponseBody
    public List<ConstructionFileEntity> upload(@RequestParam("files") MultipartFile[] files) throws IOException {
        System.out.println("test");
        return fileService.saveFiles(null, files);
    }
   
    // /**
    //  * 施工写真一覧表示
    //  */
    // @GetMapping("/{constructionId}/files")
    // public String view(
    //         @PathVariable Long constructionId,
    //         Model model
    // ) {

    //     List<ConstructionFileGroupEntity> groups =
    //             groupRepository.findByConstructionId(constructionId);

    //     for (ConstructionFileGroupEntity group : groups) {

    //         List<ConstructionFileEntity> files =
    //                 fileRepository.findByGroupId(group.getGroupId());

    //         group.setFiles(files);   // DTOにList追加しておく
    //     }

    //     model.addAttribute("constructionId", constructionId);
    //     // model.addAttribute("groups", groups);
    //     model.addAttribute("groups", fileService.getGroupsWithFiles(constructionId));

    //     return "construction/files";   // thymeleaf
    // }
}