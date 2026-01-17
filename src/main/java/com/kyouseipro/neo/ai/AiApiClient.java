package com.kyouseipro.neo.ai;

public interface AiApiClient {
    String call(String systemPrompt, String userPrompt);
}