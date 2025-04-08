package huy.project.payment_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS(0, "Success", HttpStatus.OK),
    SAVE_PAYMENT_FAILED(100002, "Save payment failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UN_AUTHORIZED(100003, "Unauthorized", HttpStatus.UNAUTHORIZED),
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