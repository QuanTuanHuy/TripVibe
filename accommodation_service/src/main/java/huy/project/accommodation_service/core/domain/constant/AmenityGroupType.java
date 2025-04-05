package huy.project.accommodation_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum AmenityGroupType {
    ACCOMMODATION("Accommodation"),
    UNIT("Unit");

    private final String type;

    AmenityGroupType(String type) {
        this.type = type;
    }

    public static String of(String type) {
        for (AmenityGroupType amenityGroupType : values()) {
            if (amenityGroupType.type.equals(type)) {
                return amenityGroupType.type;
            }
        }
        return null;
    }
}