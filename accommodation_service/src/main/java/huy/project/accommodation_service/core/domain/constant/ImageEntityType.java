package huy.project.accommodation_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum ImageEntityType {
    ACCOMMODATION("Accommodation"),
    UNIT("Unit")
    ;

    ImageEntityType(String type) {
        this.type = type;
    }
    private final String type;
}
