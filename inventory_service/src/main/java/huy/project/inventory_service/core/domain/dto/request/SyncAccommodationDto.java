package huy.project.inventory_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncAccommodationDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private List<SyncUnitDto> units;
}
