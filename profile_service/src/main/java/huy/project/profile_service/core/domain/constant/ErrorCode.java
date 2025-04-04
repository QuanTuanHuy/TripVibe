package huy.project.profile_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS(0, "Success", HttpStatus.OK),
    TOURIST_ALREADY_EXISTS(100002, "Tourist already exists", HttpStatus.BAD_REQUEST),
    TOURIST_NOT_FOUND(100003, "Tourist not found", HttpStatus.NOT_FOUND),
    FRIEND_NOT_FOUND(100004, "Friend not found", HttpStatus.NOT_FOUND),
    DELETE_FRIEND_FORBIDDEN(100005, "Delete friend not allowed", HttpStatus.FORBIDDEN),
    USER_PROFILE_NOT_FOUND(100006, "User profile not found", HttpStatus.NOT_FOUND),
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