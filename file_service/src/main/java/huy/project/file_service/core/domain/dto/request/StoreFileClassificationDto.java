package huy.project.file_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class StoreFileClassificationDto {
    String category;
    String referenceId;
    String referenceType;
    String[] tags;
    String description;
    Boolean isPublic;
    String groupId;
}
