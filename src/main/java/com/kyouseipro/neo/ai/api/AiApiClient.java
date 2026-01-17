package com.kyouseipro.neo.ai.api;

public interface AiApiClient {

    String call(String systemPrompt, String userPrompt);
}