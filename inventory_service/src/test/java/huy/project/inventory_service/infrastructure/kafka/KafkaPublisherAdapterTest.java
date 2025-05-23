package huy.project.inventory_service.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import huy.project.inventory_service.core.domain.constant.TopicConstant;
import huy.project.inventory_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.kernel.property.KafkaServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherAdapterTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaServer defaultKafkaServer;

    @InjectMocks
    private KafkaPublisherAdapter kafkaPublisherAdapter;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the KafkaServer.getBootstrapServers() method to return a server address
        Mockito.when(defaultKafkaServer.getBootstrapServers()).thenReturn("localhost:9094");

        // Mock the ObjectMapper to return a JSON string when writeValueAsString is called
        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"value\"}");
    }

    @Test
    void testPushAsyncSuccessfully() {
        // Arrange
        SyncUnitDto message = SyncUnitDto.builder()
                .accommodationId(1L)
                .unitId(2L)
                .unitNameId(1L)
                .basePrice(BigDecimal.valueOf(100))
                .unitName("Test Unit")
                .quantity(1)
                .build();

        KafkaBaseDto<SyncUnitDto> kafkaBaseDto = KafkaBaseDto.<SyncUnitDto>builder()
                .cmd(TopicConstant.AccommodationCommand.SYNC_UNIT)
                .data(message)
                .build();

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(future);

        // Act
        kafkaPublisherAdapter.pushAsync(kafkaBaseDto, TopicConstant.BookingCommand.TOPIC, null);

        // Assert
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
    }
}
