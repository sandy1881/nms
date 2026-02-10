package com.nms.nms.ai.controller;

import com.nms.nms.ai.service.NmsChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OpenAICompatController {

    private final NmsChatService chatService;

    // ⭐ This makes WebUI see a model
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

    // ⭐ Chat endpoint
    @PostMapping("/chat/completions")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {

        List<Map<String, String>> messages =
                (List<Map<String, String>>) request.get("messages");

        String userMessage = messages.get(messages.size() - 1).get("content");

        String aiResponse = chatService.askQuestion(userMessage);

        return Map.of(
                "choices", List.of(
                        Map.of(
                                "message", Map.of(
                                        "role", "assistant",
                                        "content", aiResponse
                                )
                        )
                )
        );
    }
}
