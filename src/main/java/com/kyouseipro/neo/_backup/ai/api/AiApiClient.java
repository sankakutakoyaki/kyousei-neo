package com.kyouseipro.neo._backup.ai.api;

public interface AiApiClient {

    String call(String systemPrompt, String userPrompt);
}