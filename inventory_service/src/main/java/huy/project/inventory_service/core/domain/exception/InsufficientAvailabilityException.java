package huy.project.inventory_service.core.domain.exception;

public class InsufficientAvailabilityException extends RuntimeException {
    public InsufficientAvailabilityException(String message) {
        super(message);
    }

    public InsufficientAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}
