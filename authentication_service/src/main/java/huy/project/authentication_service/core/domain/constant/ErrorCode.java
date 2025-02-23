package huy.project.authentication_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    ROLE_NAME_EXISTED(100002, "Role name is existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(100003, "Role not found", HttpStatus.NOT_FOUND),
    PRIVILEGE_NAME_EXISTED(100004, "Privilege name is existed", HttpStatus.BAD_REQUEST),
    PRIVILEGE_NOT_FOUND(100005, "Privilege not found", HttpStatus.NOT_FOUND),
    USER_NAME_EXISTED(100006, "User name is existed", HttpStatus.BAD_REQUEST),
    USER_EMAIL_EXISTED(100007, "email is existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(100008, "User not found", HttpStatus.NOT_FOUND),
    GENERATE_TOKEN_FAILED(100009, "Generate token failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(100010, "user name or password wrong", HttpStatus.UNAUTHORIZED),
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