package huy.project.ai_service.core.port;

import huy.project.ai_service.core.domain.model.DocumentModel;

import java.util.List;
import java.util.Optional;

public interface IDocumentRepoPort {
    DocumentModel save(DocumentModel document);

    Optional<DocumentModel> getDocumentById(Long id);

    void deleteDocumentById(Long id);

    List<DocumentModel> getAllDocuments();
}
