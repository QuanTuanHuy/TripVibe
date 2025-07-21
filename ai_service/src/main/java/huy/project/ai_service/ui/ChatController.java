package huy.project.ai_service.ui;

import huy.project.ai_service.core.domain.dto.request.ChatRequest;
import huy.project.ai_service.core.domain.dto.request.RAGChatRequest;
import huy.project.ai_service.core.domain.dto.response.AccommodationInfo;
import huy.project.ai_service.core.domain.dto.response.RAGChatResponse;
import huy.project.ai_service.core.service.IChatService;
import huy.project.ai_service.core.service.IRAGChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/chat")
public class ChatController {
    private final IChatService chatService;
    private final IRAGChatService ragChatService;

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/structure")
    public ResponseEntity<List<AccommodationInfo>> chatWithStructureOutput(@RequestBody ChatRequest request) {
        var response = chatService.chatWithStructureOutput(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/image")
    public ResponseEntity<String> chatWithImage(
            @RequestPart("file") MultipartFile file,
            @RequestPart("message") String message) {
        String response = chatService.chatWithImage(file, message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rag")
    public ResponseEntity<RAGChatResponse> ragChat(@RequestBody RAGChatRequest request) {
        RAGChatResponse response = ragChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simple")
    public ResponseEntity<String> simpleChat(@RequestBody ChatRequest request) {
        String response = ragChatService.simpleChat(request.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RAGChatResponse.SourceDocument>> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int maxResults
    ) {
        List<RAGChatResponse.SourceDocument> documents =
                ragChatService.searchSimilarDocuments(query, maxResults);
        return ResponseEntity.ok(documents);
    }
}
