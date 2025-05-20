package huy.project.inventory_service.core.domain.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Object... args) {
        super(String.format(message, args));
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
