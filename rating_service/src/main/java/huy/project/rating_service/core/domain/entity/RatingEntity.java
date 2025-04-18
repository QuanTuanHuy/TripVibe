package huy.project.rating_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingEntity {
    private Long id;
    private Long userId;
    private Long accommodationId;
    private Long unitId;
    private Double value;
    private String comment;
    private Long languageId;
    private Long createdAt;
    private Integer numberOfHelpful;
    private Integer numberOfUnhelpful;
}