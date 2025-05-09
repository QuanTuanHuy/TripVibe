package huy.project.rating_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum RatingCriteriaType {
    CLEANLINESS("Sạch sẽ"),
    LOCATION("Vị trí"),
    STAFF("Nhân viên"),
    VALUE_FOR_MONEY("Đáng giá tiền"),
    COMFORT("Thoải mái"),
    FACILITIES("Cơ sở vật chất");

    private final String displayName;

    RatingCriteriaType(String displayName) {
        this.displayName = displayName;
    }
}