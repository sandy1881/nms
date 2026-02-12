package com.nms.nms.ai.controller;

import com.nms.nms.ai.service.NmsChatService;
import com.nms.nms.ai.service.PdfGeneratorService;
import com.nms.nms.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OpenAICompatController {

    private final NmsChatService chatService;
    private final PdfGeneratorService pdfService;
    private final EmailService emailService;

    // Stores last meaningful AI response (used for "above chat")
    private String lastMeaningfulAnswer = "";

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
        String msg = userMessage == null ? "" : userMessage.toLowerCase();

        System.out.println("USER MESSAGE RECEIVED = " + userMessage);

        // =========================
        // 📧 EMAIL REQUEST
        // Example:
        // "send mail of above chat to abc@mail.com"
        // =========================
        if (msg.contains("send") && msg.contains("@")) {

            String email = extractEmail(userMessage);

            if (email != null) {

                // If no previous chat exists, generate fallback content
                if (lastMeaningfulAnswer == null || lastMeaningfulAnswer.isEmpty()) {
                    lastMeaningfulAnswer = chatService.askQuestion("Show latest KPIs");
                }

                // Generate PDF from last chat
                pdfService.generatePdfAndSave(lastMeaningfulAnswer, "ai-report");

                emailService.sendMailWithAttachment(
                        email,
                        "NMS AI Report",
                        lastMeaningfulAnswer,
                        "/tmp/" + pdfService.getLastGeneratedFileName()
                );

                return response("Email sent successfully to " + email);
            }
        }

        // =========================
        // 📄 PDF REQUEST (SMART LOGIC)
        // =========================
        if (msg.contains("pdf")) {

            String contentForPdf;

            // Case 1: User says "above chat"
            if (msg.contains("above") || msg.contains("previous")) {
                contentForPdf = lastMeaningfulAnswer;
            }
            // Case 2: Direct request
            // "generate pdf for last 10 health metrics"
            else if (msg.contains("generate pdf for")) {
                contentForPdf = chatService.askQuestion(userMessage);
                lastMeaningfulAnswer = contentForPdf;
            }
            // Case 3: Fallback to last response
            else if (lastMeaningfulAnswer != null && !lastMeaningfulAnswer.isEmpty()) {
                contentForPdf = lastMeaningfulAnswer;
            }
            // Case 4: Final fallback
            else {
                contentForPdf = chatService.askQuestion("Show latest KPIs");
                lastMeaningfulAnswer = contentForPdf;
            }

            String fileName =
                    pdfService.generatePdfAndSave(contentForPdf, userMessage);


            return response(
                    "PDF generated successfully.\n\nDownload here:\n["
                            + fileName + "](http://localhost:8080/download/" + fileName + ")"
            );



        }

        // =========================
        // 🧠 NORMAL AI CHAT
        // =========================
        String aiResponse = chatService.askQuestion(userMessage);

        // Save only meaningful AI answers
        if (!msg.contains("follow-up") &&
                !msg.contains("suggest") &&
                !msg.contains("json")) {

            lastMeaningfulAnswer = aiResponse;
        }

        return response(aiResponse);
    }

    // Standard OpenAI-style response
    private Map<String, Object> response(String text) {
        return Map.of(
                "id", "chatcmpl",
                "object", "chat.completion",
                "choices", List.of(
                        Map.of(
                                "index", 0,
                                "finish_reason", "stop",
                                "message", Map.of(
                                        "role", "assistant",
                                        "content", text
                                )
                        )
                )
        );
    }

    // Extract email from text
    private String extractEmail(String text) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group();
        return null;
    }
}
