package huy.project.rating_service.core.domain.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingDto {
    private Long id;
    private Double value;
    private String comment;
    private Long languageId;
    private Long createdAt;

    private UnitDto unit;
    private UserProfileDto user;
}
