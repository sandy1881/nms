package com.nms.nms.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OLLAMA_EMBED_URL = "http://ollama:11434/api/embeddings";

    public String generateEmbedding(String text) {
        Map<String, Object> body = Map.of(
                "model", "nomic-embed-text",
                "prompt", text
        );

        Map response = restTemplate.postForObject(OLLAMA_EMBED_URL, body, Map.class);
        return response.get("embedding").toString();
    }
}
