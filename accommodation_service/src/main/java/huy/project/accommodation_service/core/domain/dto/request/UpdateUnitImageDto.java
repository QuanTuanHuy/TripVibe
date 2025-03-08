package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUnitImageDto {
    List<CreateImageDto> newImages;
    private List<Long> deleteImageIds;
}
