package huy.project.accommodation_service.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmenityEntity {
    private Long id;
    private String name;
    private String description;
    private Long groupId;
}
