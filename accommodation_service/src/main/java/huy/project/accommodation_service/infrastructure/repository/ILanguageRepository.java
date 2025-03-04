package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.LanguageModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILanguageRepository extends IBaseRepository<LanguageModel> {
    Optional<LanguageModel> findByName(String name);
    Optional<LanguageModel> findByCode(String code);
    List<LanguageModel> findByIdIn(List<Long> ids);
}
