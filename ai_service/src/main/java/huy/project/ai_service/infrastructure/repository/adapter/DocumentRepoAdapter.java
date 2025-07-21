package huy.project.ai_service.infrastructure.repository.adapter;

import huy.project.ai_service.core.domain.constant.ErrorCode;
import huy.project.ai_service.core.domain.model.DocumentModel;
import huy.project.ai_service.core.exception.AppException;
import huy.project.ai_service.core.port.IDocumentRepoPort;
import huy.project.ai_service.infrastructure.repository.IDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DocumentRepoAdapter implements IDocumentRepoPort {
    private final IDocumentRepository documentRepository;

    @Override
    public DocumentModel save(DocumentModel document) {
        try {
            return documentRepository.save(document);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SAVE_DOCUMENT_ERROR);
        }
    }

    @Override
    public Optional<DocumentModel> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    public void deleteDocumentById(Long id) {
        try {
            documentRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException(ErrorCode.DELETE_DOCUMENT_ERROR);
        }
    }

    @Override
    public List<DocumentModel> getAllDocuments() {
        return documentRepository.findAll();
    }
}
