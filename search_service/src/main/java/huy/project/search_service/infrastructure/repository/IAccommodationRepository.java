package huy.project.search_service.infrastructure.repository;

import huy.project.search_service.infrastructure.repository.document.AccommodationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccommodationRepository extends ElasticsearchRepository<AccommodationDocument, Long> {
}
