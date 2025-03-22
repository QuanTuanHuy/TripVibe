package huy.project.file_service.core.domain.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(100001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SUCCESS(0, "Success", HttpStatus.OK),
    FILE_STORAGE_ERROR(100002, "File storage error", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_EMPTY(100003, "File is empty", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(100004, "File is too large", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_SUPPORTED(100005, "File type is not supported", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND(100006, "File not found", HttpStatus.NOT_FOUND),
    DELETE_FILE_ERROR(100007, "Could not delete file", HttpStatus.INTERNAL_SERVER_ERROR),
    FORBIDDEN_DELETE_FILE(100008, "Forbidden to delete file", HttpStatus.FORBIDDEN)
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
