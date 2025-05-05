package huy.project.accommodation_service.ui.graphql;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.service.IAccommodationTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccommodationTypeGraphQLController {
    IAccommodationTypeService accTypeService;

    @QueryMapping
    public List<AccommodationTypeEntity> accommodationTypes() {
        var params = AccommodationTypeParams.builder().build();
        return accTypeService.getAccommodationTypes(params);
    }
}
