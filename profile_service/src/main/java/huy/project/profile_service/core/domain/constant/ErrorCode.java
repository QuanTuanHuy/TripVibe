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
    WISHLIST_ITEM_ALREADY_EXISTS(100007, "Wishlist item already exists", HttpStatus.BAD_REQUEST),
    WISHLIST_NOT_FOUND(100008, "Wishlist not found", HttpStatus.NOT_FOUND),
    FORBIDDEN_GET_WISHLIST(100009, "Forbidden to get wishlist", HttpStatus.FORBIDDEN),
    ACCOMMODATION_NOT_FOUND(100010, "Accommodation not found", HttpStatus.NOT_FOUND),
    SAVE_WISHLIST_ITEM_FAILED(100011, "Save wishlist item failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SAVE_WISHLIST_FAILED(100012, "Save wishlist failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_FILE_FAILED(100013, "Upload file failed", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(100014, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
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