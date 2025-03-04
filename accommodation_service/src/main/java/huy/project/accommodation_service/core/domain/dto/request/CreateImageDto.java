package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateImageDto {
    private String url;
    private Boolean isPrimary;
}
