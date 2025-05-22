package huy.project.inventory_service.core.domain.callback;

import java.util.function.Consumer;

/**
 * Factory for creating callback instances with lambda expressions
 */
public class KafkaCallbackFactory {
    
    /**
     * Creates a callback with the provided success and failure handlers
     * 
     * @param onSuccess Handler for a success case
     * @param onFailure Handler for a failure case
     * @return A KafkaOperationCallback instance
     */
    public static KafkaOperationCallback create(Consumer<String> onSuccess, Consumer<Throwable> onFailure) {
        return new KafkaOperationCallback() {
            @Override
            public void onSuccess(String result) {
                onSuccess.accept(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                onFailure.accept(ex);
            }
        };
    }
}
