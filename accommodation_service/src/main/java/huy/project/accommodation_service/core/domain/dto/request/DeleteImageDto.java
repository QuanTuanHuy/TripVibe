package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DeleteImageDto {
    private List<Long> imageIds;
}
