package com.kyouseipro.neo.ai.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.ai.AiApiClient;
import com.kyouseipro.neo.ai.entity.AiKnowledgeChunk;
import com.kyouseipro.neo.ai.entity.AiPromptTemplate;
import com.kyouseipro.neo.ai.reepository.AiAskLogRepository;
import com.kyouseipro.neo.ai.reepository.AiKnowledgeChunkRepository;
import com.kyouseipro.neo.ai.reepository.AiPromptTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiQuestionService {

    private final AiKnowledgeChunkRepository chunkRepo;
    private final AiPromptTemplateRepository promptRepo;
    private final AiAskLogRepository logRepo;
    private final AiApiClient aiApi; // ← 有料AI

    public String ask(String question) {

        List<AiKnowledgeChunk> chunks =
            chunkRepo.findForQuestion(question);

        String knowledgeText = chunks.stream()
            .map(c -> "・" + c.content)
            .collect(Collectors.joining("\n\n"));

        AiPromptTemplate prompt =
            promptRepo.findActiveByName("DEFAULT");

        String userPrompt = prompt.userPromptFormat
            .replace("${knowledge}", knowledgeText)
            .replace("${question}", question);

        String answer = aiApi.call(
            prompt.systemPrompt,
            userPrompt
        );

        logRepo.insert(question, chunks.size(), answer);

        return answer;
    }
}