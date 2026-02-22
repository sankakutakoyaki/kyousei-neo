// package com.kyouseipro.neo.common.file.controller;

// import java.io.IOException;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.List;
// import java.util.Map;

// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;

// import com.kyouseipro.neo.common.file.entity.ConstructionFileDto;
// import com.kyouseipro.neo.common.file.entity.FileEntity;
// import com.kyouseipro.neo.common.file.entity.GroupRequest;
// import com.kyouseipro.neo.common.file.repository.ConstructionFileRepository;
// import com.kyouseipro.neo.common.file.service.ConstructionFileGroupService;
// import com.kyouseipro.neo.common.file.service.ConstructionFileService;
// import com.kyouseipro.neo.common.file.service.EntityFileService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/files")
// public class EntityFileController {



//     // private final EntityFileService service;

//     // @GetMapping("/{entity}/{id}")
//     // public List<EntityFile> list(
//     //         @PathVariable String entity,
//     //         @PathVariable Long id) {
//     //     return service.list(entity, id);
//     // }

//     // @PostMapping("/{entity}/{id}")
//     // public EntityFile upload(
//     //         @PathVariable String entity,
//     //         @PathVariable Long id,
//     //         @RequestParam("group") String group,
//     //         @RequestParam("file") MultipartFile file) throws IOException {
//     //     return service.upload(entity, id, group, file);
//     // }

//     // @DeleteMapping("/{fileId}")
//     // public void delete(@PathVariable Long fileId) {
//     //     service.delete(fileId);
//     // }

//     // @GetMapping("/view/{fileId}")
//     // public ResponseEntity<Resource> view(@PathVariable Long fileId) throws IOException {

//     //     EntityFile f = service.findById(fileId);

//     //     Path path = Paths.get("uploads").resolve(f.getFolderName())
//     //                     .resolve(f.getInternalName());

//     //     Resource resource = new UrlResource(path.toUri());

//     //     return ResponseEntity.ok()
//     //         .contentType(MediaType.parseMediaType(f.getContentType()))
//     //         .body(resource);
//     // }
// }