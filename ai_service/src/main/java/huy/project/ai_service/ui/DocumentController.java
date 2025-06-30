package huy.project.ai_service.ui;

import huy.project.ai_service.core.domain.dto.request.DocumentUploadRequest;
import huy.project.ai_service.core.domain.model.DocumentModel;
import huy.project.ai_service.core.service.IDocumentIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/documents")
public class DocumentController {
    private final IDocumentIngestionService documentIngestionService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentModel> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title
    ) {

        DocumentUploadRequest request = new DocumentUploadRequest();
        request.setFile(file);
        request.setTitle(title);

        DocumentModel document = documentIngestionService.uploadDocument(request);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    public ResponseEntity<List<DocumentModel>> getAllDocuments() {
        return ResponseEntity.ok(documentIngestionService.getAllDocuments());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentModel>> searchDocuments(@RequestParam String query) {
        List<DocumentModel> documents = documentIngestionService.searchDocuments(query);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String documentId) {
        documentIngestionService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }
}
