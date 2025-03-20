package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddUnitToAccElasticMessage {
    private Long accommodationId;
    private CreateUnitElasticMessage unit;

    public static AddUnitToAccElasticMessage from(UnitEntity unit) {
        return AddUnitToAccElasticMessage.builder()
                .accommodationId(unit.getAccommodationId())
                .unit(CreateUnitElasticMessage.from(unit))
                .build();
    }

    public KafkaBaseDto<AddUnitToAccElasticMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<AddUnitToAccElasticMessage>builder()
                .cmd(TopicConstant.SearchCommand.ADD_UNIT_TO_ACC)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }
}
