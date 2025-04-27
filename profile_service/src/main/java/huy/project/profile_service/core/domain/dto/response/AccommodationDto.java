package huy.project.profile_service.core.domain.dto.response;

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
    private List<Object> units;
}
