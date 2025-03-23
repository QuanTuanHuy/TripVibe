package huy.project.rating_service.core.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRatingDto {
    private Long userId;
    private Long accommodationId;
    private Long unitId;
    private Double value;
    private String comment;
    private Long languageId;
}
