package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.response.DailyPriceDto;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceGroupEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPricingRulePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CalculateDynamicPriceUseCase {
    IPricingRulePort pricingRulePort;
    GetUnitUseCase getUnitUseCase;
    GetUnitPriceGroupUseCase getUnitPriceGroupUseCase;

    @Transactional(readOnly = true)
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        Long unitId = request.getUnitId();
        LocalDate startDate = request.getCheckInDate();
        LocalDate endDate = request.getCheckOutDate();
        int guestCount = request.getGuestCount();

        // Get base price from price group
        List<UnitPriceGroupEntity> priceGroups = getUnitPriceGroupUseCase.getUnitPriceGroupsByUnitId(unitId);
        if (priceGroups.isEmpty()) {
            log.error("No price groups found for unitId: {}", unitId);
            throw new AppException(ErrorCode.PRICE_GROUP_NOT_FOUND);
        }

        UnitPriceGroupEntity basePrice = priceGroups.stream()
                .filter(pg -> pg.getNumberOfGuests().equals((long)guestCount))
                .findFirst()
                .orElse(priceGroups.get(0));

        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        // Get all pricing rules applicable for this date range
        List<PricingRuleEntity> applicableRules = pricingRulePort.getPricingRulesByUnitIdAndDateRange(unitId, startDate, endDate)
                .stream()
                .filter(PricingRuleEntity::getIsActive)
                .sorted(Comparator.comparing(PricingRuleEntity::getPriority).reversed())
                .toList();

        // Calculate daily prices
        List<DailyPriceDto> dailyPrices = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDate currentDate = startDate;

        BigDecimal dayBasePrice1 = BigDecimal.valueOf(1.0 * unit.getPricePerNight() * basePrice.getPercentage() / 100);

        while (currentDate.isBefore(endDate)) {
            BigDecimal dayBasePrice = dayBasePrice1;

            // Apply all applicable rules for this specific day
            for (PricingRuleEntity rule : applicableRules) {
                if (isRuleApplicable(rule, currentDate, guestCount)) {
                    dayBasePrice = applyPricingRule(dayBasePrice, rule);
                }
            }

            // Round to 2 decimal places
            dayBasePrice = dayBasePrice.setScale(2, RoundingMode.HALF_UP);
            totalPrice = totalPrice.add(dayBasePrice);

            dailyPrices.add(new DailyPriceDto(currentDate, dayBasePrice));
            currentDate = currentDate.plusDays(1);
        }

        return PriceCalculationResponse.builder()
                .unitId(unitId)
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .guestCount(guestCount)
                .basePrice(dayBasePrice1)
                .totalPrice(totalPrice)
                .dailyPrices(dailyPrices)
                .build();
    }

    private boolean isRuleApplicable(PricingRuleEntity rule, LocalDate date, int guestCount) {
        // Check date range
        if (date.isBefore(rule.getStartDate()) || date.isAfter(rule.getEndDate())) {
            return false;
        }

        // Check occupancy constraints if present
        if (rule.getMinOccupancy() != null && guestCount < rule.getMinOccupancy()) {
            return false;
        }

        if (rule.getMaxOccupancy() != null && guestCount > rule.getMaxOccupancy()) {
            return false;
        }

        return true;
    }

    private BigDecimal applyPricingRule(BigDecimal basePrice, PricingRuleEntity rule) {
        if ("PERCENTAGE".equals(rule.getAdjustmentType())) {
            // Apply percentage adjustment
            BigDecimal adjustmentMultiplier = BigDecimal.ONE.add(
                    rule.getAdjustmentValue().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
            );
            return basePrice.multiply(adjustmentMultiplier);
        } else if ("FIXED_AMOUNT".equals(rule.getAdjustmentType())) {
            // Apply fixed amount adjustment
            return basePrice.add(rule.getAdjustmentValue());
        }

        return basePrice;
    }
}
