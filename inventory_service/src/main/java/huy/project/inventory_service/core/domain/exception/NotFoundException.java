package huy.project.inventory_service.core.domain.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
