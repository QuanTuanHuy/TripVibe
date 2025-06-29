package huy.project.ai_service.ui;

import huy.project.ai_service.core.dto.request.ChatRequest;
import huy.project.ai_service.core.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/chat")
public class ChatController {
    private final IChatService chatService;

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request);
        return ResponseEntity.ok(response);
    }
}
