package com.kyouseipro.neo.ai.controller;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class MockAiController {

    private static final List<String> ANSWERS = List.of(
        "それはあなたの感想ですよね？",
        "知らんがな。",
        "それくらいググってください。",
        "そんなこともわからないんですかwww",
        "ご質問ありがとうございます。全然わかりません。"
    );

    private final Random random = new Random();

    @PostMapping("/mock")
    public Map<String, String> mock(@RequestBody Map<String, String> req) {

        // String question = req.get("question"); // 今は使わなくてOK

        String answer = ANSWERS.get(random.nextInt(ANSWERS.size()));

        return Map.of(
            "answer", answer
        );
    }
}