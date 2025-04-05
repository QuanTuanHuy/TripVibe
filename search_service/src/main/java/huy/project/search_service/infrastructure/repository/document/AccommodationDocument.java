package huy.project.search_service.infrastructure.repository.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "accommodations")
public class AccommodationDocument {
    @Id
    private Long id;

    private Long typeId;
    private String name;
    private Double ratingStar;
    private Boolean isVerified;

    private List<Long> amenityIds;
    private List<Long> bookingPolicyIds;

    @Field(type = FieldType.Object)
    private LocationDocument location;

    @Field(type = FieldType.Nested)
    private List<UnitDocument> units;
}
