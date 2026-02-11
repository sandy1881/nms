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

    // Model listing for OpenWebUI dropdown
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

        // 🔴 Intercept BEFORE LLM: trigger PDF on 'pdf' or 'report'
        if (msg.contains("pdf") || msg.contains("report")) {

            // Get latest KPI explanation text from your existing AI flow
            String reportText = chatService.askQuestion("Show latest KPIs");

            // Generate PDF from that text
            byte[] pdfBytes = pdfService.generatePdf(reportText);
            String base64Pdf = pdfService.toBase64(pdfBytes);

            return Map.of(
                    "id", "chatcmpl-pdf",
                    "object", "chat.completion",
                    "choices", List.of(
                            Map.of(
                                    "index", 0,
                                    "finish_reason", "stop",
                                    "message", Map.of(
                                            "role", "assistant",
                                            "content", "PDF generated from latest KPI report.",
                                            // IMPORTANT: OpenWebUI expects files inside message.files
                                            "files", List.of(
                                                    Map.of(
                                                            "type", "file",
                                                            "name", "kpi-report.pdf",
                                                            "mime_type", "application/pdf",
                                                            "data", base64Pdf
                                                    )
                                            )
                                    )
                            )
                    )
            );
        }

        // Normal AI chat flow
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
