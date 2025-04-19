package huy.project.accommodation_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS(0, "Success", HttpStatus.OK),
    AMENITY_GROUP_NAME_EXISTED(100002, "Amenity group name existed", HttpStatus.BAD_REQUEST),
    AMENITY_GROUP_NOT_FOUND(100003, "Amenity group not found", HttpStatus.NOT_FOUND),
    AMENITY_NAME_EXISTED(100004, "Amenity name existed", HttpStatus.BAD_REQUEST),
    AMENITY_NOT_FOUND(100005, "Amenity not found", HttpStatus.NOT_FOUND),
    LANGUAGE_NAME_EXISTED(100006, "language name existed", HttpStatus.BAD_REQUEST),
    LANGUAGE_CODE_EXISTED(100007, "language code existed", HttpStatus.BAD_REQUEST),
    LANGUAGE_NOT_FOUND(100008, "language not found", HttpStatus.NOT_FOUND),
    UNIT_NAME_EXISTED(100009, "unit name existed", HttpStatus.BAD_REQUEST),
    UNIT_NAME_NOT_FOUND(100010, "unit name not found", HttpStatus.NOT_FOUND),
    BED_TYPE_NAME_EXISTED(100010, "bed type name existed", HttpStatus.BAD_REQUEST),
    BED_TYPE_NOT_FOUND(100011, "bed type not found", HttpStatus.NOT_FOUND),
    LOCATION_NOT_FOUND(100011, "Location not found", HttpStatus.NOT_FOUND),
    ACCOMMODATION_TYPE_NAME_EXISTED(100012, "Accommodation type name existed", HttpStatus.BAD_REQUEST),
    PRICE_TYPE_NOT_FOUND(100013, "Price type not found", HttpStatus.NOT_FOUND),
    ACCOMMODATION_NAME_EXISTED(100014, "Accommodation name existed", HttpStatus.BAD_REQUEST),
    ACCOMMODATION_NOT_FOUND(100015, "Accommodation not found", HttpStatus.NOT_FOUND),
    ACCOMMODATION_AMENITY_NOT_FOUND(100016, "Accommodation amenity not found", HttpStatus.NOT_FOUND),
    UNIT_NOT_FOUND(100016, "Unit not found", HttpStatus.NOT_FOUND),
    UNIT_IMAGE_NOT_FOUND(100017, "Unit image not found", HttpStatus.NOT_FOUND),
    UNIT_AMENITY_NOT_FOUND(100018, "Unit amenity not found", HttpStatus.NOT_FOUND),
    FORBIDDEN_DELETE_UNIT(100019, "Forbidden to delete unit", HttpStatus.FORBIDDEN),
    FORBIDDEN_RESTORE_UNIT(100020, "Forbidden to restore unit", HttpStatus.FORBIDDEN),
    UNIT_ALREADY_DELETED(100020, "Unit already deleted", HttpStatus.BAD_REQUEST),
    UNIT_NOT_DELETED(100021, "Unit not deleted", HttpStatus.BAD_REQUEST),
    SERVICE_UNAVAILABLE(100022, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    ;


    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}