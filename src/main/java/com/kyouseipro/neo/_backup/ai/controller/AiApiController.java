// package com.kyouseipro.neo._backup.ai.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.kyouseipro.neo._backup.ai.service.AiQuestionService;

// import lombok.RequiredArgsConstructor;

// @Controller
// @RequiredArgsConstructor
// public class AiApiController {
//     private final AiQuestionService aiQuestionService;
    
//     @PostMapping("/ai/ask")
//     public String ask(@RequestParam String question, Model model) {
//         String answer = aiQuestionService.ask(question);
//         model.addAttribute("answer", answer);
//         return "/";
//     }
// }
