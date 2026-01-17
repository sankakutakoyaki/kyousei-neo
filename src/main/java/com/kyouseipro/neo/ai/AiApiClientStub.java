package com.kyouseipro.neo.ai;

public class AiApiClientStub implements AiApiClient {

    @Override
    public String call(String systemPrompt, String userPrompt) {

        // ① knowledge 部分だけ抜き出す（簡易）
        String knowledge = extractKnowledge(userPrompt);

        if (knowledge == null || knowledge.isBlank()) {
            return "該当する社内情報が見つかりませんでした。";
        }

        // ② 「AIっぽい」体裁にするだけ
        return """
        【社内ルールより】
        %s
        """.formatted(knowledge);
    }

    private String extractKnowledge(String prompt) {
        int start = prompt.indexOf("【社内情報】");
        if (start == -1) return null;

        return prompt.substring(start + "【社内情報】".length()).trim();
    }
}