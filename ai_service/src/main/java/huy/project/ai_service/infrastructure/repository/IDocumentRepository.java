package huy.project.ai_service.infrastructure.repository;

import huy.project.ai_service.core.domain.model.DocumentModel;
import org.springframework.stereotype.Repository;

@Repository
public interface IDocumentRepository extends IBaseRepository<DocumentModel> {
}
