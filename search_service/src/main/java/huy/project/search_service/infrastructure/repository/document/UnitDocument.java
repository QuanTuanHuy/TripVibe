package huy.project.search_service.infrastructure.repository.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitDocument {
    @Field(type = FieldType.Long)
    private Long id;
    @Field(type = FieldType.Double)
    private BigDecimal pricePerNight;
    private Integer maxAdults;
    private Integer maxChildren;

    private Integer quantity;

    private List<Long> amenityIds;

    @Field(type = FieldType.Nested)
    private List<BedroomDocument> bedrooms;

    @Field(type = FieldType.Nested)
    private List<UnitAvailabilityDocument> availability;
}
