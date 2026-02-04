package com.nms.nms.ai.service;

import com.nms.nms.ai.tools.KpiToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NmsChatService {

    private final EmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;
    private final KpiToolService kpiToolService;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OLLAMA_CHAT_URL = "http://ollama:11434/api/generate";

    public String askQuestion(String question) {

        // 1️⃣ Tool usage
        String kpiInfo = kpiToolService.getLatestKpiSummary();

        // 2️⃣ Retrieve memory
        String memoryContext = vectorSearchService.getAllKnowledge()
                .stream()
                .limit(3)
                .map(doc -> doc.getContent())
                .reduce("", (a, b) -> a + "\n" + b);

        // 3️⃣ Prompt for LLM
        String prompt = """
                You are an AI assistant for a Network Monitoring System.

                KPI DATA:
                %s

                PAST KNOWLEDGE:
                %s

                USER QUESTION:
                %s

                Give a professional technical answer.
                """.formatted(kpiInfo, memoryContext, question);

        Map<String, Object> body = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        Map response = restTemplate.postForObject(OLLAMA_CHAT_URL, body, Map.class);
        return response.get("response").toString();
    }
}
