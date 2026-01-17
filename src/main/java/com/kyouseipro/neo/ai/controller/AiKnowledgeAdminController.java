package com.kyouseipro.neo.ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kyouseipro.neo.ai.reepository.AiKnowledgeRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/ai/knowledge")
public class AiKnowledgeAdminController {

    private final AiKnowledgeRepository aiKnowledgeRepository;

    // public AiKnowledgeAdminController(AiKnowledgeRepository knowledgeRepo) {
    //     this.knowledgeRepo = knowledgeRepo;
    // }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("list", aiKnowledgeRepository.findAllActive());
        return "admin/ai/knowledge-list";
    }
}