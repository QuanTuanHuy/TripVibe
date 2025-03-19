package huy.project.search_service.core.domain.dto.kafka;

import huy.project.search_service.core.domain.entity.UnitEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AddUnitToAccMessage {
    private Long accommodationId;
    private UnitEntity unit;
}
