package huy.project.accommodation_service.core.domain.dto.response;

import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDto {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private Long hostId;
    private Long typeId;
    private Boolean isVerified;
    private List<UnitDto> units;

    public static AccommodationDto from(AccommodationEntity entity) {
        return AccommodationDto.builder()
                .id(entity.getId())
                .hostId(entity.getHostId())
                .typeId(entity.getTypeId())
                .name(entity.getName())
                .thumbnailUrl(entity.getThumbnailUrl())
                .isVerified(entity.getIsVerified())
                .units(entity.getUnits().stream().map(UnitMapper.INSTANCE::toDto).toList())
                .build();
    }
}
