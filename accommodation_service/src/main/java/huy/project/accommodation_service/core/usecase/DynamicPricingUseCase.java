package huy.project.accommodation_service.core.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.request.PricingRuleParams;
import huy.project.accommodation_service.core.domain.dto.response.DailyPriceDto;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IUnitPriceCalendarPort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DynamicPricingUseCase {
    IUnitPriceCalendarPort unitPriceCalendarPort;
    GetPricingRuleUseCase getPricingRuleUseCase;
    ObjectMapper objectMapper;

    GetUnitUseCase getUnitUseCase;
    GetUnitPricingCalendarUseCase getUnitPricingCalendarUseCase;
    PriceCalculationCacheUseCase priceCalculationCacheUseCase;

    AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public List<UnitPriceCalendarEntity> updateBasePrice(Long userId, Long unitId, LocalDate startDate, LocalDate endDate,
                                                         BigDecimal basePrice, String updateSource) {
        // Validate owner
        var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, unitId);
        if (!isOwnerOfUnit.getFirst()) {
            log.error("User {} is not owner of unit {}", userId, unitId);
            throw new AppException(ErrorCode.FORBIDDEN_UPDATE_BASE_PRICE);
        }

        List<UnitPriceCalendarEntity> updatedPrices = new ArrayList<>();
        List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).toList();

        List<UnitPriceCalendarEntity> priceCalendars = getUnitPricingCalendarUseCase
                .getUnitPricingCalendars(unitId, startDate, endDate);
        var priceCalendarMap = priceCalendars.stream()
                .collect(Collectors.toMap(
                        UnitPriceCalendarEntity::getDate, priceCalendar -> priceCalendar));

        for (LocalDate date : dates) {
            UnitPriceCalendarEntity priceCalendar = priceCalendarMap.get(date);

            priceCalendar.setBasePrice(basePrice);
            priceCalendar.setLastUpdated(Instant.now().toEpochMilli());
            priceCalendar.setUpdateSource(updateSource);

        }

        // Save updated prices to the database
        unitPriceCalendarPort.saveAll(priceCalendars);

        // After updating base prices, apply all applicable rules
        applyPricingRules(unitId, startDate, endDate);

        return updatedPrices;
    }

    @Transactional
    public void applyPricingRules(Long unitId, LocalDate startDate, LocalDate endDate) {
        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        // Get all active pricing rules for this unit
        PricingRuleParams params = PricingRuleParams.builder()
                .unitId(unit.getId())
                .accommodationId(unit.getAccommodationId())
                .startDate(startDate)
                .endDate(endDate)
                .build();
        List<PricingRuleEntity> rules = getPricingRuleUseCase.getPricingRules(params);

        // Sort by priority (higher priority rules applied later to override lower priority rules)
        rules.sort(Comparator.comparing(PricingRuleEntity::getPriority));

        List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).toList();

        List<UnitPriceCalendarEntity> priceCalendars = getUnitPricingCalendarUseCase
                .getUnitPricingCalendars(unitId, startDate, endDate);
        var priceCalendarMap = priceCalendars.stream()
                .collect(Collectors.toMap(UnitPriceCalendarEntity::getDate, priceCalendar -> priceCalendar));

        for (LocalDate date : dates) {
            UnitPriceCalendarEntity priceCalendar = priceCalendarMap.get(date);

            // Reset multipliers before applying rules
            priceCalendar.setWeekendMultiplier(BigDecimal.ONE);
            priceCalendar.setHolidayMultiplier(BigDecimal.ONE);
            priceCalendar.setSeasonalMultiplier(BigDecimal.ONE);
            priceCalendar.setEarlyBirdDiscount(BigDecimal.ZERO);
            priceCalendar.setLastMinuteDiscount(BigDecimal.ZERO);

            // Apply each applicable rule
            for (PricingRuleEntity rule : rules) {
                if (isRuleApplicable(rule, date)) {
                    applyRule(priceCalendar, rule);
                }
            }

            priceCalendar.setLastUpdated(Instant.now().toEpochMilli());
            priceCalendar.setUpdateSource("RULE_ENGINE");

            unitPriceCalendarPort.save(priceCalendar);
        }
    }

    private boolean isRuleApplicable(PricingRuleEntity rule, LocalDate date) {
        // Check if date is within rule date range
        if (date.isBefore(rule.getStartDate()) || date.isAfter(rule.getEndDate())) {
            return false;
        }

        // Check day of week for weekend rules
        if ("WEEKEND".equals(rule.getRuleType())) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return switch (dayOfWeek) {
                case MONDAY -> rule.getApplyMonday();
                case TUESDAY -> rule.getApplyTuesday();
                case WEDNESDAY -> rule.getApplyWednesday();
                case THURSDAY -> rule.getApplyThursday();
                case FRIDAY -> rule.getApplyFriday();
                case SATURDAY -> rule.getApplySaturday();
                case SUNDAY -> rule.getApplySunday();
            };
        }

        return true;
    }

    private void applyRule(UnitPriceCalendarEntity priceCalendar, PricingRuleEntity rule) {
        switch (rule.getRuleType()) {
            case "WEEKEND":
                priceCalendar.setWeekendMultiplier(rule.getPriceMultiplier());
                break;

            case "HOLIDAY":
                priceCalendar.setHolidayMultiplier(rule.getPriceMultiplier());
                break;

            case "SEASON":
                priceCalendar.setSeasonType(rule.getName()); // e.g., "HIGH_SEASON"
                priceCalendar.setSeasonalMultiplier(rule.getPriceMultiplier());
                break;

            case "EARLY_BIRD":
                priceCalendar.setEarlyBirdDays(rule.getDayThreshold());
                priceCalendar.setEarlyBirdDiscount(BigDecimal.ONE.subtract(rule.getPriceMultiplier()));
                break;

            case "LAST_MINUTE":
                priceCalendar.setLastMinuteDays(rule.getDayThreshold());
                priceCalendar.setLastMinuteDiscount(BigDecimal.ONE.subtract(rule.getPriceMultiplier()));
                break;

            case "LOS":
                try {
                    // Parse and update LOS discounts
                    if (rule.getAdditionalParams() != null) {
                        priceCalendar.setLosDiscounts(rule.getAdditionalParams());
                    }
                } catch (Exception e) {
                    log.error("Error parsing LOS rule parameters: {}", e.getMessage());
                }
                break;

            case "OCCUPANCY":
                if (rule.getAdditionalParams() != null) {
                    try {
                        Map<String, String> params = objectMapper.readValue(
                                rule.getAdditionalParams(),
                                objectMapper.getTypeFactory().constructMapType(
                                        HashMap.class, String.class, Object.class)
                        );

                        Integer baseOccupancy = Integer.parseInt(params.get("baseOccupancy"));
                        BigDecimal extraPersonFee = new BigDecimal(params.get("extraPersonFee"));

                        log.info("baseOccupancy: {}, extraPersonFee: {}", baseOccupancy, extraPersonFee);
                        priceCalendar.setBaseOccupancy(baseOccupancy);
                        priceCalendar.setExtraPersonFee(extraPersonFee);
                    } catch (Exception e) {
                        log.error("Error parsing occupancy rule parameters: {}", e.getMessage());
                    }
                }
                break;
        }
    }

    public BigDecimal calculateFinalPrice(
            UnitPriceCalendarEntity priceCalendar,
            LocalDate bookingDate,
            Integer lengthOfStay,
            Integer guestCount
    ) {
        BigDecimal finalPrice = priceCalendar.getBasePrice();

        // Apply seasonal, weekend, and holiday multipliers
        finalPrice = finalPrice
                .multiply(priceCalendar.getSeasonalMultiplier())
                .multiply(priceCalendar.getWeekendMultiplier())
                .multiply(priceCalendar.getHolidayMultiplier());

        // Apply early bird discount if applicable
        long daysInAdvance = bookingDate.until(priceCalendar.getDate()).getDays();

        if (priceCalendar.getEarlyBirdDays() != null &&
                daysInAdvance >= priceCalendar.getEarlyBirdDays() &&
                priceCalendar.getEarlyBirdDiscount().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = finalPrice.multiply(BigDecimal.ONE.subtract(priceCalendar.getEarlyBirdDiscount()));
        }

        // Apply last minute discount if applicable
        if (priceCalendar.getLastMinuteDays() != null &&
                daysInAdvance <= priceCalendar.getLastMinuteDays() &&
                priceCalendar.getLastMinuteDiscount().compareTo(BigDecimal.ZERO) > 0) {
            finalPrice = finalPrice.multiply(BigDecimal.ONE.subtract(priceCalendar.getLastMinuteDiscount()));
        }

        // Apply LOS discount
        if (priceCalendar.getLosDiscounts() != null && lengthOfStay != null) {
            try {
                Map<String, String> losDiscounts = objectMapper.readValue(
                        priceCalendar.getLosDiscounts(),
                        objectMapper.getTypeFactory().constructMapType(
                                HashMap.class, String.class, String.class)
                );

                // Find the highest applicable LOS discount
                BigDecimal maxDiscount = BigDecimal.ZERO;
                for (Map.Entry<String, String> entry : losDiscounts.entrySet()) {
                    int los = Integer.parseInt(entry.getKey());
                    BigDecimal discount = new BigDecimal(entry.getValue());
                    if (lengthOfStay >= los && discount.compareTo(maxDiscount) > 0) {
                        maxDiscount = discount;
                    }
                }

                if (maxDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    finalPrice = finalPrice.multiply(BigDecimal.ONE.subtract(maxDiscount));
                }
            } catch (Exception e) {
                log.error("Error applying LOS discount: {}", e.getMessage());
            }
        }

        // Apply occupancy-based pricing
        if (priceCalendar.getBaseOccupancy() != null &&
                priceCalendar.getExtraPersonFee() != null &&
                guestCount != null &&
                guestCount > priceCalendar.getBaseOccupancy()) {

            int extraPersons = guestCount - priceCalendar.getBaseOccupancy();
            BigDecimal extraPersonFees = priceCalendar.getExtraPersonFee().multiply(BigDecimal.valueOf(extraPersons));
            finalPrice = finalPrice.add(extraPersonFees);
        }

        return finalPrice;
    }

    @Transactional
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        var cachedPrice = priceCalculationCacheUseCase.getFromCache(request);
        if (cachedPrice != null) {
            return cachedPrice;
        }

        Long unitId = request.getUnitId();
        LocalDate startDate = request.getCheckInDate();
        LocalDate endDate = request.getCheckOutDate();
        int guestCount = request.getGuestCount();
        Integer lengthOfStay = Math.toIntExact(startDate.until(endDate, ChronoUnit.DAYS));

        List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).toList();
        if (dates.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }

        List<UnitPriceCalendarEntity> priceCalendars = getUnitPricingCalendarUseCase.getUnitPricingCalendars(unitId, startDate, endDate);
        var priceCalendarMap = priceCalendars.stream()
                .collect(Collectors.toMap(UnitPriceCalendarEntity::getDate, priceCalendar -> priceCalendar));

        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        // Calculate daily prices
        List<DailyPriceDto> dailyPrices = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        LocalDate currentDate = startDate;
        LocalDate now = LocalDate.now();

        for (var date : dates) {
            var priceCalendar = priceCalendarMap.get(date);
            if (priceCalendar == null) {
                log.warn("No price calendar entry found for unit {} on {}, creating default entry", unitId, date);
                priceCalendar = UnitPriceCalendarEntity.newPriceCalendar(unit, date);
                unitPriceCalendarPort.save(priceCalendar);
            }

            var priceOfDate = calculateFinalPrice(priceCalendar, now, lengthOfStay, guestCount);

            totalPrice = totalPrice.add(priceOfDate);

            dailyPrices.add(new DailyPriceDto(currentDate, priceOfDate));
            currentDate = currentDate.plusDays(1);
        }

        var response = PriceCalculationResponse.builder()
                .unitId(unitId)
                .checkInDate(startDate)
                .checkOutDate(endDate)
                .guestCount(guestCount)
                .basePrice(unit.getPricePerNight())
                .totalPrice(totalPrice)
                .dailyPrices(dailyPrices)
                .build();

        // Cache the result
        priceCalculationCacheUseCase.saveToCache(request, response);

        return response;
    }
}
