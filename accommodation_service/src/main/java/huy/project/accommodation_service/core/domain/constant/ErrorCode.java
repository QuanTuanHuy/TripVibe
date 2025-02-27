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