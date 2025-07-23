package huy.project.ai_service.ui;

import huy.project.ai_service.core.domain.dto.request.ChatRequest;
import huy.project.ai_service.core.domain.dto.request.RAGChatRequest;
import huy.project.ai_service.core.domain.dto.response.AccommodationInfo;
import huy.project.ai_service.core.domain.dto.response.RAGChatResponse;
import huy.project.ai_service.core.service.IChatService;
import huy.project.ai_service.core.service.IRAGChatService;
import huy.project.ai_service.ui.resource.Resource;
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
    public ResponseEntity<Resource<String>> chat(@RequestBody ChatRequest request) {
        String response = chatService.chat(request);
        return ResponseEntity.ok(new Resource<>(response));
    }

    @PostMapping("/structure")
    public ResponseEntity<Resource<List<AccommodationInfo>>> chatWithStructureOutput(@RequestBody ChatRequest request) {
        var response = chatService.chatWithStructureOutput(request);
        return ResponseEntity.ok(new Resource<>(response));
    }


    @PostMapping("/image")
    public ResponseEntity<Resource<String>> chatWithImage(
            @RequestPart("file") MultipartFile file,
            @RequestPart("message") String message) {
        String response = chatService.chatWithImage(file, message);
        return ResponseEntity.ok(new Resource<>(response));
    }

    @PostMapping("/rag")
    public ResponseEntity<Resource<RAGChatResponse>> ragChat(@RequestBody RAGChatRequest request) {
        RAGChatResponse response = ragChatService.chat(request);
        return ResponseEntity.ok(new Resource<>(response));
    }

    @PostMapping("/simple")
    public ResponseEntity<Resource<String>> simpleChat(@RequestBody ChatRequest request) {
        String response = ragChatService.simpleChat(request.getMessage());
        return ResponseEntity.ok(new Resource<>(response));
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
