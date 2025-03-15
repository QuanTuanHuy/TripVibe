package huy.project.search_service.infrastructure.repository.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitAvailabilityDocument {
    @Field(type = FieldType.Date)
    private Date date;

    @Field(type = FieldType.Integer)
    private Integer availableCount;
}
