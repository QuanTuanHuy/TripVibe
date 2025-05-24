package huy.project.inventory_service.infrastructure.redis;

import huy.project.inventory_service.kernel.property.RedisLockProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class RedisLockIntegrationTest {

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.0"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
        registry.add("app.redis.lock.namespace", () -> "test-lock");
        registry.add("app.redis.lock.expiration", () -> 30000);
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisLockProperty redisLockProperty;

    private RedisLock redisLock;

    private static final String TEST_LOCK_ID = "test-lock-id";

    @BeforeEach
    void setUp() {
        // Create a fresh instance of RedisLock for each test
        redisLock = new RedisLock(redisTemplate, redisLockProperty);
        
        // Clean up any lock that might exist from previous tests
        redisTemplate.delete(redisLockProperty.getNamespace() + "." + TEST_LOCK_ID);
    }

    @Test
    void acquireLock_shouldSuccessfullyAcquireLock() {
        // Act
        redisLock.acquireLock(TEST_LOCK_ID);

        // Assert
        String key = redisLockProperty.getNamespace() + "." + TEST_LOCK_ID;
        assertTrue(redisTemplate.hasKey(key));
    }
    
    @Test
    void releaseLock_shouldReleasePreviouslyAcquiredLock() {
        // Arrange
        redisLock.acquireLock(TEST_LOCK_ID);
        
        // Act
        redisLock.releaseLock(TEST_LOCK_ID);
        
        // Assert
        String key = redisLockProperty.getNamespace() + "." + TEST_LOCK_ID;
        assertNotEquals(Boolean.TRUE, redisTemplate.hasKey(key));
    }
    
    @Test
    void acquireLock_withTimeout_shouldReturnTrueIfLockAcquired() {
        // Act
        boolean acquired = redisLock.acquireLock(TEST_LOCK_ID, 1000, TimeUnit.MILLISECONDS);
        
        // Assert
        assertTrue(acquired);
        String key = redisLockProperty.getNamespace() + "." + TEST_LOCK_ID;
        assertTrue(redisTemplate.hasKey(key));
    }
    
    @Test
    void acquireLock_withTimeoutAndLeaseTime_shouldRespectLeaseTime() throws InterruptedException {
        // Arrange
        int leaseTimeMs = 500;
        
        // Act
        boolean acquired = redisLock.acquireLock(TEST_LOCK_ID, 1000, leaseTimeMs, TimeUnit.MILLISECONDS);
        
        // Assert initial state
        assertTrue(acquired);
        String key = redisLockProperty.getNamespace() + "." + TEST_LOCK_ID;
        assertTrue(redisTemplate.hasKey(key));
        
        // Wait for the lease time to expire
        Thread.sleep(leaseTimeMs * 2);
        
        // The lock should be automatically released after lease time
        assertFalse(redisTemplate.hasKey(key));
    }

    @Test
    void acquireLock_whenLockIsAlreadyAcquired_shouldWaitUntilReleased() throws InterruptedException {
        // Arrange
        CountDownLatch lockAcquired = new CountDownLatch(1);
        CountDownLatch lockReleased = new CountDownLatch(1);
        CountDownLatch secondLockAcquired = new CountDownLatch(1);
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Act
        // Thread 1 acquires the lock
        executor.submit(() -> {
            try {
                redisLock.acquireLock(TEST_LOCK_ID);
                lockAcquired.countDown();
                
                // Hold the lock for a while
                Thread.sleep(500);
                
                // Release the lock
                redisLock.releaseLock(TEST_LOCK_ID);
                lockReleased.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Wait for first thread to acquire the lock
        assertTrue(lockAcquired.await(5, TimeUnit.SECONDS));
        
        // Thread 2 tries to acquire the lock
        executor.submit(() -> {
            try {
                redisLock.acquireLock(TEST_LOCK_ID);
                secondLockAcquired.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // The second thread should not have acquired the lock yet
        assertFalse(secondLockAcquired.await(200, TimeUnit.MILLISECONDS));
        
        // Wait for the first thread to release the lock
        assertTrue(lockReleased.await(5, TimeUnit.SECONDS));
        
        // Now the second thread should have acquired the lock
        assertTrue(secondLockAcquired.await(5, TimeUnit.SECONDS));
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    @Test
    void concurrentAccess_shouldPreventRaceCondition() throws InterruptedException {
        // Arrange
        int numThreads = 10;
        int incrementsPerThread = 100;
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        // Act
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        redisLock.acquireLock(TEST_LOCK_ID);
                        try {
                            // Simulate a critical section
                            int current = counter.get();
                            // Add a small delay to increase chance of race condition if lock isn't working
                            Thread.sleep(1);
                            counter.set(current + 1);
                        } finally {
                            redisLock.releaseLock(TEST_LOCK_ID);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        // Assert
        assertEquals(numThreads * incrementsPerThread, counter.get());
    }
    
    @Test
    void deleteLock_shouldForceDeleteLock() {
        // Arrange
        redisLock.acquireLock(TEST_LOCK_ID);
        
        // Act
        redisLock.deleteLock(TEST_LOCK_ID);
        
        // Assert
        String key = redisLockProperty.getNamespace() + "." + TEST_LOCK_ID;
        assertFalse(redisTemplate.hasKey(key));
    }
}
