package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccommodationLanguageEntity {
    private Long id;
    private Long accommodationId;
    private Long languageId;
}
