package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUnitRepository extends IBaseRepository<UnitModel> {
    List<UnitModel> findByAccommodationId(Long accommodationId);
    Optional<UnitModel> findByAccommodationIdAndId(Long accommodationId, Long id);
    void deleteByAccommodationId(Long accId);
    List<UnitModel> findByIdIn(List<Long> ids);

    List<UnitModel> findByAccommodationIdIn(List<Long> ids);
}
