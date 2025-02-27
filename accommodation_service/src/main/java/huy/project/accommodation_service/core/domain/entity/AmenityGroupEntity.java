package huy.project.accommodation_service.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmenityGroupEntity {
    private Long id;
    private String name;
    private String description;
    private List<AmenityEntity> amenities;
}
