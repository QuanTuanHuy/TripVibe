package huy.project.accommodation_service.infrastructure.repository;

import feign.Param;
import huy.project.accommodation_service.infrastructure.repository.model.PricingRuleModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPricingRuleRepository extends IBaseRepository<PricingRuleModel> {
    List<PricingRuleModel> findByUnitId(Long unitId);

//    @Query("SELECT p FROM PricingRuleModel p WHERE p.unitId = ?1 AND p.startDate <= ?2 AND p.endDate >= ?3")
//    List<PricingRuleModel> findByUnitIdAndDateRange(Long unitId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM PricingRuleModel p WHERE p.unitId = :unitId AND " +
            "p.isActive = true AND " +
            "((p.startDate IS NULL AND p.endDate IS NULL) OR " +
            " (p.startDate IS NULL AND p.endDate >= :startDate) OR" +
            " (p.endDate IS NULL AND p.startDate <= :endDate))")
    List<PricingRuleModel> findActiveRulesByUnitIdAndDateRange(
            @Param("unitId") Long unitId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
