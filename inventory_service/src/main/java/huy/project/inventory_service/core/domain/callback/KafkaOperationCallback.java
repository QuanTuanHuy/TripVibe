package huy.project.inventory_service.core.domain.callback;

/**
 * Callback interface for asynchronous Kafka operations
 */
public interface KafkaOperationCallback {
    /**
     * Called when the operation completes successfully
     * @param result The result of the operation
     */
    void onSuccess(String result);
    
    /**
     * Called when the operation fails
     * @param ex The exception that occurred
     */
    void onFailure(Throwable ex);
}
