package com.nms.nms.ai.service;

import com.nms.nms.ai.model.KnowledgeDocument;
import com.nms.nms.service.KpiMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NmsChatService {

    private final VectorSearchService vectorSearchService;
    private final KpiMetricService kpiService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ollama.base-url}")
    private String ollamaUrl;

    @Value("${ollama.chat.model}")
    private String model;

    public String askQuestion(String question) {

        // 🔹 STEP 1: Load all knowledge from DB (current RAG setup)
        List<String> knowledge = vectorSearchService.getAllKnowledge()
                .stream()
                .map(KnowledgeDocument::getContent)
                .toList();

        // 🔹 STEP 2: Prepare conversation messages
        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", """
You are an AI NMS expert assistant.

You have access to tools that fetch real KPI data.
Use tools whenever user asks about:
- temperature
- optical power
- cpu
- memory
- performance
- trends
- metrics

Do NOT guess KPI values.
Always call tools when data is required.
"""
        ));

        messages.add(Map.of(
                "role", "user",
                "content", """
Knowledge Base:
%s

User Question:
%s
""".formatted(String.join("\n", knowledge), question)
        ));

        // 🔹 STEP 3: Define available tools (your real APIs)
        List<Map<String, Object>> tools = List.of(

                tool("getKpiSummary",
                        "Get average, max, min for a KPI",
                        Map.of("kpiName", "string")),

                tool("getHourlyTrend",
                        "Get hourly trend for a KPI",
                        Map.of("kpiName", "string")),

                tool("getDailyTrend",
                        "Get daily trend for a KPI",
                        Map.of("kpiName", "string")),

                tool("getLatestKpis",
                        "Get latest KPI records",
                        Map.of())
        );

        // 🔹 STEP 4: First call → let LLM decide which API to use
        Map<String, Object> firstResponse = restTemplate.postForObject(
                ollamaUrl + "/api/chat",
                Map.of(
                        "model", model,
                        "messages", messages,
                        "tools", tools,
                        "stream", false
                ),
                Map.class
        );

        Map message = (Map) firstResponse.get("message");

        // 🔹 STEP 5: If LLM requested a tool
        if (message.containsKey("tool_calls")) {

            List<Map<String, Object>> calls =
                    (List<Map<String, Object>>) message.get("tool_calls");

            for (Map<String, Object> call : calls) {

                Map function = (Map) call.get("function");
                String name = function.get("name").toString();
                Map args = (Map) function.get("arguments");

                Object result = executeTool(name, args);

                // Add tool call to conversation
                messages.add(Map.of(
                        "role", "assistant",
                        "tool_calls", List.of(call)
                ));

                // Add tool result
                messages.add(Map.of(
                        "role", "tool",
                        "name", name,
                        "content", result.toString()
                ));
            }

            // 🔹 STEP 6: Second call → generate final explanation
            Map<String, Object> finalResponse = restTemplate.postForObject(
                    ollamaUrl + "/api/chat",
                    Map.of(
                            "model", model,
                            "messages", messages,
                            "stream", false
                    ),
                    Map.class
            );

            return ((Map) finalResponse.get("message")).get("content").toString();
        }

        // 🔹 If no tool needed, return direct answer
        return message.get("content").toString();
    }

    // 🔹 Executes actual Java service methods based on LLM choice
    private Object executeTool(String name, Map args) {

        return switch (name) {

            case "getKpiSummary" ->
                    kpiService.getKpiSummary(args.get("kpiName").toString());

            case "getHourlyTrend" ->
                    kpiService.getHourlyTrend(args.get("kpiName").toString());

            case "getDailyTrend" ->
                    kpiService.getDailyTrend(args.get("kpiName").toString());

            case "getLatestKpis" ->
                    kpiService.getLatestKpis();

            default -> "Unknown tool requested";
        };
    }

    // 🔹 Builds tool schema for Ollama
    private Map<String, Object> tool(String name, String desc, Map<String, String> params) {

        Map<String, Object> properties = new HashMap<>();

        params.forEach((k, v) ->
                properties.put(k, Map.of("type", v)));

        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", name,
                        "description", desc,
                        "parameters", Map.of(
                                "type", "object",
                                "properties", properties
                        )
                )
        );
    }
}
