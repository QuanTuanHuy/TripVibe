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
    ACCOMMODATION_NOT_FOUND(100006, "Accommodation not found", HttpStatus.NOT_FOUND),
    UPDATE_RATING_SUMMARY_FAILED(100007, "Update rating summary failed", HttpStatus.INTERNAL_SERVER_ERROR),
    RATING_NOT_FOUND(100008, "Rating not found", HttpStatus.NOT_FOUND),
    FORBIDDEN_CREATE_RATING_RESPONSE(100009, "Forbidden to create rating response", HttpStatus.FORBIDDEN),
    SAVE_RATING_RESPONSE_FAILED(100010, "Save rating response failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SAVE_RATING_HELPFUL_FAILED(100011, "Save rating helpful failed", HttpStatus.INTERNAL_SERVER_ERROR),
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