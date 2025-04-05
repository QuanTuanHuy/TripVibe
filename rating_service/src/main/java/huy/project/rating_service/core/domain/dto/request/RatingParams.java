package huy.project.rating_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class RatingParams extends BaseParams {
    private Double minValue;
    private Double maxValue;
    private Long languageId;
    private Long createdFrom;
    private Long createdTo;
    private Long accommodationId;
    private Long unitId;
}
