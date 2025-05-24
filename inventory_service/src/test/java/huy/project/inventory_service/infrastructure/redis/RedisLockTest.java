package huy.project.inventory_service.infrastructure.redis;

import huy.project.inventory_service.kernel.property.RedisLockProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("deprecation")
class RedisLockTest {

    @Mock(lenient = true)
    private RedisTemplate<String, String> redisTemplate;

    @Mock(lenient = true)
    private ValueOperations<String, String> valueOperations;

    private RedisLock redisLock;

    private static final String LOCK_NAMESPACE = "test-lock";
    private static final long DEFAULT_LEASE_TIME = 30000;
    private static final String TEST_LOCK_ID = "test-lock-id";

    @BeforeEach
    void setUp() {
        RedisLockProperty redisLockProperty = new RedisLockProperty();
        redisLockProperty.setNamespace(LOCK_NAMESPACE);
        redisLockProperty.setExpiration(DEFAULT_LEASE_TIME);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisLock = new RedisLock(redisTemplate, redisLockProperty);
    }

    @Test
    void acquireLock_shouldSetLockValue() {
        // Arrange
        when(valueOperations.setIfAbsent(
                anyString(), anyString(), any(Duration.class)
        )).thenReturn(true);

        // Act
        redisLock.acquireLock(TEST_LOCK_ID);

        // Assert
        verify(valueOperations).setIfAbsent(
                eq(LOCK_NAMESPACE + "." + TEST_LOCK_ID),
                anyString(),
                eq(Duration.ofMillis(DEFAULT_LEASE_TIME))
        );
    }

    @Test
    void acquireLock_shouldRetryUntilSuccess() {
        // Arrange
        when(valueOperations.setIfAbsent(
                anyString(), anyString(), any(Duration.class)
        )).thenReturn(false, false, true);

        // Act
        redisLock.acquireLock(TEST_LOCK_ID);

        // Assert
        verify(valueOperations, times(3)).setIfAbsent(
                eq(LOCK_NAMESPACE + "." + TEST_LOCK_ID),
                anyString(),
                eq(Duration.ofMillis(DEFAULT_LEASE_TIME))
        );
    }

    @Test
    void acquireLock_withTimeout_shouldReturnTrueOnSuccess() {
        // Arrange
        when(valueOperations.setIfAbsent(
                anyString(), anyString(), any(Duration.class)
        )).thenReturn(true);

        // Act
        boolean result = redisLock.acquireLock(TEST_LOCK_ID, 1000, TimeUnit.MILLISECONDS);

        // Assert
        assertTrue(result);
        verify(valueOperations).setIfAbsent(
                eq(LOCK_NAMESPACE + "." + TEST_LOCK_ID),
                anyString(),
                eq(Duration.ofMillis(DEFAULT_LEASE_TIME))
        );
    }

    @Test
    void acquireLock_withTimeout_shouldReturnFalseOnFailure() {
        // Arrange
        when(valueOperations.setIfAbsent(
                anyString(), anyString(), any(Duration.class)
        )).thenReturn(false);

        // Act
        boolean result = redisLock.acquireLock(TEST_LOCK_ID, 200, TimeUnit.MILLISECONDS);

        // Assert
        assertFalse(result);
        verify(valueOperations, atLeastOnce()).setIfAbsent(
                eq(LOCK_NAMESPACE + "." + TEST_LOCK_ID),
                anyString(),
                eq(Duration.ofMillis(DEFAULT_LEASE_TIME))
        );
    }

    @Test
    void acquireLock_withTimeoutAndLeaseTime_shouldReturnTrueOnSuccess() {
        // Arrange
        when(valueOperations.setIfAbsent(
                anyString(), anyString(), any(Duration.class)
        )).thenReturn(true);

        // Act
        boolean result = redisLock.acquireLock(TEST_LOCK_ID, 1000, 5000, TimeUnit.MILLISECONDS);

        // Assert
        assertTrue(result);
        verify(valueOperations).setIfAbsent(
                eq(LOCK_NAMESPACE + "." + TEST_LOCK_ID),
                anyString(),
                eq(Duration.ofMillis(5000))
        );
    }

    @Test
    void releaseLock_shouldCallRedisDelete() {
        // Arrange
        String lockId = TEST_LOCK_ID;

        // Act
        redisLock.releaseLock(lockId);

        // Assert
        verify(redisTemplate).delete(LOCK_NAMESPACE + "." + lockId);
    }

    @Test
    void releaseLock_shouldHandleException() {
        // Arrange
        String lockId = TEST_LOCK_ID;
        doThrow(new RuntimeException("Test exception")).when(redisTemplate).delete(anyString());

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> redisLock.releaseLock(lockId));

        // Verify
        verify(redisTemplate).delete(LOCK_NAMESPACE + "." + lockId);
    }

    @Test
    void deleteLock_shouldCallRedisDelete() {
        // Act
        redisLock.deleteLock(TEST_LOCK_ID);

        // Assert
        verify(redisTemplate).delete(LOCK_NAMESPACE + "." + TEST_LOCK_ID);
    }

    @Test
    void acquireLock_withNullLockId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            redisLock.acquireLock(null);
        });
    }

    @Test
    void acquireLock_withEmptyLockId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            redisLock.acquireLock("");
        });
    }
}
