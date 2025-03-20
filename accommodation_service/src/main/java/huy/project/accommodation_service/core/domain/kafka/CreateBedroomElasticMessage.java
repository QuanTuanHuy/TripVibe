package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateBedroomElasticMessage {
    private Long id;
    private Integer quantity;
    private List<CreateBedElasticMessage> beds;

    public static CreateBedroomElasticMessage from(BedroomEntity bedroom) {
        return CreateBedroomElasticMessage.builder()
                .id(bedroom.getId())
                .quantity(bedroom.getQuantity())
                .beds(bedroom.getBeds().stream().map(CreateBedElasticMessage::from).toList())
                .build();
    }
}
