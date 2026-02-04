package com.nms.nms.ai.controller;

import com.nms.nms.ai.service.NmsChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class NmsChatController {

    private final NmsChatService chatService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String answer = chatService.askQuestion(question);
        return Map.of("answer", answer);
    }
}
