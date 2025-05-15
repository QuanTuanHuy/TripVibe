package com.booking.inventory.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class KafkaConfig {
    
    // Topic for receiving booking events
    @Bean
    public NewTopic bookingEventsTopic() {
        return TopicBuilder.name("booking-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    // Topic for receiving accommodation events
    @Bean
    public NewTopic accommodationEventsTopic() {
        return TopicBuilder.name("accommodation-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public StringJsonMessageConverter jsonConverter() {
        return new StringJsonMessageConverter();
    }
}
