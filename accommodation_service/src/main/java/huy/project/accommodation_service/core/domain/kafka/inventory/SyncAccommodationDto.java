package huy.project.accommodation_service.core.domain.kafka.inventory;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.domain.kafka.KafkaBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncAccommodationDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private List<SyncUnitDto> units;

    public KafkaBaseDto<SyncAccommodationDto> toKafkaBaseDto() {
        return KafkaBaseDto.<SyncAccommodationDto>builder()
                .cmd(TopicConstant.InventoryCommand.SYNC_ACCOMMODATION)
                .data(this)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static SyncAccommodationDto from(AccommodationEntity accommodation) {
        return SyncAccommodationDto.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .address(accommodation.getLocation().getDetailAddress())
                .units(accommodation.getUnits().stream()
                        .map(SyncUnitDto::from)
                        .toList())
                .build();
    }
}