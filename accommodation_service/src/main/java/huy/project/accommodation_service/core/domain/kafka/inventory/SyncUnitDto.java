package huy.project.accommodation_service.core.domain.kafka.inventory;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.kafka.KafkaBaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncUnitDto {
    private Long accommodationId;
    private Long unitId;
    private Long unitNameId;
    private BigDecimal basePrice;
    private String unitName;
    private Integer quantity;

    public static SyncUnitDto from(UnitEntity unit) {
        return SyncUnitDto.builder()
                .accommodationId(unit.getAccommodationId())
                .unitId(unit.getId())
                .unitNameId(unit.getUnitNameId())
                .basePrice(unit.getPricePerNight())
                .unitName(unit.getUnitName().getName())
                .quantity(unit.getQuantity())
                .build();
    }

    public KafkaBaseDto<SyncUnitDto> toKafkaBaseDto() {
        return KafkaBaseDto.<SyncUnitDto>builder()
                .cmd(TopicConstant.InventoryCommand.SYNC_UNIT)
                .data(this)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
