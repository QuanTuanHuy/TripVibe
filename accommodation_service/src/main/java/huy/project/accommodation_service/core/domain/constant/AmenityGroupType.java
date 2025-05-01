package huy.project.accommodation_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum AmenityGroupType {
    ACCOMMODATION("Accommodation"),
    UNIT("Unit"),
    Bathroom("Bathroom"),
    PROPERTY("Property"),
    SERVICE("Service"),
    FACILITY("Facility"),
    COMMON("Common"),
    OTHER("Other"),
    PARKING("Parking"),
    POOL("Pool"),
    GYM("Gym"),
    KITCHEN("Kitchen"),
    LIVING_ROOM("Living Room"),
    ;

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