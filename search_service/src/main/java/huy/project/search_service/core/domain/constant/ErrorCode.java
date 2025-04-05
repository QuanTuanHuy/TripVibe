package huy.project.search_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS(0, "Success", HttpStatus.OK),
    ACCOMMODATION_NOT_FOUND(100002, "Accommodation not found", HttpStatus.NOT_FOUND),
    ACCOMMODATION_ALREADY_EXISTS(100003, "Accommodation already exists", HttpStatus.BAD_REQUEST),
    UNIT_NOT_FOUND(100003, "Unit not found", HttpStatus.NOT_FOUND),
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