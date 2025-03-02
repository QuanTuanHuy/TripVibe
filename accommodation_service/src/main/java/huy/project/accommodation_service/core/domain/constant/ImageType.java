package huy.project.accommodation_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum ImageType {
    ACCOMMODATION("Accommodation"),
    UNIT("Unit")
    ;

    ImageType(String type) {
        this.type = type;
    }
    private final String type;
}
