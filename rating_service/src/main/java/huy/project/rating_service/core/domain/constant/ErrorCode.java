package huy.project.rating_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS(0, "Success", HttpStatus.OK),
    RATING_ALREADY_EXIST(100002, "Rating already exist", HttpStatus.BAD_REQUEST),
    INVALID_RATING_VALUE(100003, "Invalid rating value", HttpStatus.BAD_REQUEST),
    SERVICE_UNAVAILABLE(100004, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    FORBIDDEN_CREATE_RATING(100005, "Forbidden to create rating", HttpStatus.FORBIDDEN),
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