package huy.project.accommodation_service.infrastructure.repository;

import feign.Param;
import huy.project.accommodation_service.infrastructure.repository.model.PricingRuleModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPricingRuleRepository extends IBaseRepository<PricingRuleModel> {
    @Query("SELECT p FROM PricingRuleModel p WHERE (p.unitId IS NULL OR p.unitId = :unitId) AND (p.accommodationId IS NULL OR p.accommodationId = :accommodationId) AND " +
            "p.isActive = true AND " +
            "((p.startDate IS NULL AND p.endDate IS NULL) OR " +
            " (p.startDate IS NULL AND p.endDate >= :startDate) OR" +
            " (p.endDate IS NULL AND p.startDate <= :endDate) OR" +
            " (p.startDate <= :endDate OR p.endDate >= :startDate))")
    List<PricingRuleModel> findActiveRules(
            @Param("unitId") Long unitId,
            @Param("accommodationId") Long accommodationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
