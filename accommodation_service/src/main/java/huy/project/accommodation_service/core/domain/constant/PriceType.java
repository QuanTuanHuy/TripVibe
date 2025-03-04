package huy.project.accommodation_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum PriceType {
    STANDARD("Standard"),
    NONREFUNDABLE("Non-refundable"),
    ;

    private final String type;

    PriceType(String type) {
        this.type = type;
    }
}
