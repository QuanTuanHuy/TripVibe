package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.PricingRuleModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPricingRuleRepository extends IBaseRepository<PricingRuleModel> {
    List<PricingRuleModel> findByUnitId(Long unitId);

    @Query("SELECT p FROM PricingRuleModel p WHERE p.unitId = ?1 AND p.startDate <= ?2 AND p.endDate >= ?3")
    List<PricingRuleModel> findByUnitIdAndDateRange(Long unitId, LocalDate startDate, LocalDate endDate);
}
