package com.nms.nms.ai.controller;

import com.nms.nms.ai.service.NmsChatService;
import com.nms.nms.ai.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OpenAICompatController {

    private final NmsChatService chatService;
    private final PdfGeneratorService pdfService;

    @GetMapping("/models")
    public Map<String, Object> models() {
        return Map.of(
                "data", List.of(
                        Map.of(
                                "id", "nms-ai",
                                "object", "model",
                                "owned_by", "nms"
                        )
                )
        );
    }

    @PostMapping("/chat/completions")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {

        List<Map<String, String>> messages =
                (List<Map<String, String>>) request.get("messages");

        String userMessage = messages.get(messages.size() - 1).get("content");
        System.out.println("USER MESSAGE RECEIVED = " + userMessage);

        String msg = userMessage == null ? "" : userMessage.toLowerCase();

        // 🔴 PDF trigger
        if (msg.contains("pdf") || msg.contains("report")) {

            String reportText = chatService.askQuestion("Show latest KPIs");

            pdfService.generatePdfAndSave(reportText);

            return Map.of(
                    "id", "chatcmpl-pdf",
                    "object", "chat.completion",
                    "choices", List.of(
                            Map.of(
                                    "index", 0,
                                    "finish_reason", "stop",
                                    "message", Map.of(
                                            "role", "assistant",
                                            "content",
                                            "PDF generated successfully.\n\nDownload here:\nhttp://localhost:8080/download/kpi"
                                    )
                            )
                    )
            );
        }

        // Normal AI flow
        String aiResponse = chatService.askQuestion(userMessage);

        return Map.of(
                "id", "chatcmpl-text",
                "object", "chat.completion",
                "choices", List.of(
                        Map.of(
                                "index", 0,
                                "finish_reason", "stop",
                                "message", Map.of(
                                        "role", "assistant",
                                        "content", aiResponse
                                )
                        )
                )
        );
    }
}
