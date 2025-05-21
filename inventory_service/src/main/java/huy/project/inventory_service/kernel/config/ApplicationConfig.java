package huy.project.inventory_service.kernel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Application configuration for caching, scheduling and async operations
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ApplicationConfig {

    /**
     * Task executor for cache warming and other background tasks
     */
    @Bean("cacheTaskExecutor")
    @Primary
    public AsyncTaskExecutor cacheTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("cache-");
        executor.initialize();
        return executor;
    }
    
    /**
     * Task executor specifically for Redis operations
     */
    @Bean("redisTaskExecutor")
    public Executor redisTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("redis-");
        executor.initialize();
        return executor;
    }
}
