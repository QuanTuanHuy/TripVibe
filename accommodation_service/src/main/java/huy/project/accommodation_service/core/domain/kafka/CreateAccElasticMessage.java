package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import lombok.*;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateAccElasticMessage {
    private Long id;
    private Long typeId;
    private String name;
    private Double ratingStar;
    private Boolean isVerified;

    private List<Long> amenityIds;
    private List<Long> bookingPolicyIds;

    private LocationMessage location;
    private List<CreateUnitElasticMessage> units;

    public static CreateAccElasticMessage from(AccommodationEntity accommodation) {
        List<Long> accAmenityIds = accommodation.getAmenities().stream()
                .map(AccommodationAmenityEntity::getAmenityId)
                .toList();
        return CreateAccElasticMessage.builder()
                .id(accommodation.getId())
                .typeId(accommodation.getTypeId())
                .name(accommodation.getName())
                .isVerified(accommodation.getIsVerified())
                .amenityIds(accAmenityIds)
                .bookingPolicyIds(List.of())
                .location(LocationMessage.from(accommodation.getLocation()))
                .units(accommodation.getUnits().stream().map(CreateUnitElasticMessage::from).toList())
                .build();
    }

    public KafkaBaseDto<CreateAccElasticMessage> toKafkaBaseDto() {
        return KafkaBaseDto.<CreateAccElasticMessage>builder()
                .cmd(TopicConstant.SearchCommand.CREATE_ACCOMMODATION)
                .timestamp(Instant.now().toEpochMilli())
                .data(this)
                .build();
    }
}
