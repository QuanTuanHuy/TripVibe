package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.DocumentUploadRequest;
import huy.project.ai_service.core.domain.model.DocumentModel;

import java.util.List;

public interface IDocumentIngestionService {
    DocumentModel uploadDocument(DocumentUploadRequest request);

    List<DocumentModel> getAllDocuments();

    void deleteDocument(String documentId);

    List<DocumentModel> searchDocuments(String query);
}
