package huy.project.accommodation_service.core.domain.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateUnitMessage {
    private Long id;
    private String name;
}
