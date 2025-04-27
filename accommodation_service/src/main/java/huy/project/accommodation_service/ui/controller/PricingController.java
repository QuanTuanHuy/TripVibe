package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreatePricingRuleDto;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.request.PricingRuleParams;
import huy.project.accommodation_service.core.domain.dto.request.UpdateBasePriceRequest;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;
import huy.project.accommodation_service.core.service.IPricingService;
import huy.project.accommodation_service.kernel.utils.AuthenUtils;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public/v1/pricing")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PricingController {
    IPricingService pricingService;

    @PostMapping("/rules")
    public ResponseEntity<PricingRuleEntity> createPricingRule(
            @RequestBody CreatePricingRuleDto request
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        var rule = pricingService.createPricingRule(userId, request);
        return ResponseEntity.ok(rule);
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Resource<?>> deletePricingRule(
            @PathVariable Long id
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        pricingService.deletePricingRule(userId, id);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @GetMapping("/rules")
    public ResponseEntity<Resource<List<PricingRuleEntity>>> getPricingRules(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long accommodationId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        var params = PricingRuleParams.builder()
                .unitId(unitId)
                .accommodationId(accommodationId)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return ResponseEntity.ok(new Resource<>(pricingService.getPricingRules(params)));
    }

    @PutMapping("/units/{unitId}")
    public ResponseEntity<Resource<List<UnitPriceCalendarEntity>>> updatePricingRule(
            @PathVariable Long unitId,
            @RequestBody UpdateBasePriceRequest request
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(pricingService.updateBasePrice(userId, unitId, request)));
    }

    @GetMapping("/units/{unitId}/calendars")
    public ResponseEntity<Resource<List<UnitPriceCalendarEntity>>> getPriceCalendars(
            @PathVariable Long unitId,
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate
    ) {
        return ResponseEntity.ok(new Resource<>(pricingService.getUnitPricingCalendars(unitId, startDate, endDate)));
    }

    @PostMapping("/calculate")
    public ResponseEntity<Resource<PriceCalculationResponse>> calculatePrice(
            @RequestBody PriceCalculationRequest request
    ) {
        return ResponseEntity.ok(new Resource<>(pricingService.calculatePrice(request)));
    }

}
