// package com.kyouseipro.neo.sql.controller;

// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.kyouseipro.neo.sql.model.SelectRequest;
// import com.kyouseipro.neo.sql.service.ExecuteService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/execute")
// @RequiredArgsConstructor
// public class ExecuteController {

//     private final ExecuteService executeService;

//     @PostMapping
//     public int execute(@RequestBody SelectRequest req) {
//         return executeService.execute(req);
//     }
// }