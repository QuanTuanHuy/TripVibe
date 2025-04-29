package huy.project.accommodation_service.infrastructure.repository.specification;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationTypeModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class AccommodationTypeSpecification {
    public static Specification<AccommodationTypeModel> getAccommodationTypes(AccommodationTypeParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(params.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + params.getName() + "%"));
            }

            if (params.getIsHighlighted() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isHighlighted"), params.getIsHighlighted()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
