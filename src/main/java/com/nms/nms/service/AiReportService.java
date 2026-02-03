package com.nms.nms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiReportService {

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateKpiIncidentReport(long tempCritical, long opticalCritical) {

        String prompt = """
            You are an AI Network Monitoring System.

            Critical KPI threshold breaches detected in the last 5 minutes:

            High Temperature events (>75°C): %d
            Low Optical Power events (< -28 dBm): %d

            Generate a professional incident report including:
            - Summary
            - Possible causes
            - Impact
            - Recommended actions
            """.formatted(tempCritical, opticalCritical);

        String url = ollamaBaseUrl + "/api/generate";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3");
        request.put("prompt", prompt);
        request.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            Map response = restTemplate.postForObject(url, entity, Map.class);

            if (response == null || !response.containsKey("response")) {
                return "AI report generation failed: Empty response from model.";
            }

            return response.get("response").toString();

        } catch (Exception ex) {
            return "AI report generation failed: " + ex.getMessage();
        }
    }
}
