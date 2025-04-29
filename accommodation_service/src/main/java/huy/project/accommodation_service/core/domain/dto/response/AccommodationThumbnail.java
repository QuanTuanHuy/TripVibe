package huy.project.accommodation_service.core.domain.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccommodationThumbnail {
    Long id;
    String name;
    String description;
    String thumbnailUrl;

    Location location;
    List<Unit> units;
    RatingSummary ratingSummary;
    PriceInfo priceInfo;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {
        String address;
        Long countryId;
        Long provinceId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class Unit {
        Long id;
        String name;
        String description;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RatingSummary {
        Double rating;
        Integer numberOfRatings;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriceInfo {
        Integer lengthOfStay;
        Integer guestCount;
        BigDecimal initialPrice;
        BigDecimal currentPrice;
    }
}
