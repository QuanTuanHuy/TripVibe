package huy.project.search_service.infrastructure.repository.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedroomDocument {
    private Long id;
    private Integer quantity;

    @Field(type = FieldType.Nested)
    private List<BedDocument> beds;
}
