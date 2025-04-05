package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateAccommodationMessage {
    private Long id;
    private String name;
    private Long ownerId;
    private List<CreateUnitMessage> units;

    public KafkaBaseDto<CreateAccommodationMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<CreateAccommodationMessage>builder()
                .cmd(TopicConstant.BookingCommand.CREATE_ACCOMMODATION)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }

    public static CreateAccommodationMessage from(AccommodationEntity accommodation) {
        return CreateAccommodationMessage.builder()
                .id(accommodation.getId())
                .ownerId(accommodation.getHostId())
                .name(accommodation.getName())
                .units(accommodation.getUnits().stream().map(unit -> CreateUnitMessage.builder()
                                .id(unit.getId())
                                .name(unit.getUnitName().getName())
                                .build())
                        .toList())
                .build();
    }
}
