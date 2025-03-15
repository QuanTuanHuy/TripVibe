package huy.project.search_service.infrastructure.repository.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedDocument {
    private Long id;
    private Integer quantity;
    private Long bedTypeId;
}
