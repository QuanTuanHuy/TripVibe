package huy.project.accommodation_service.infrastructure.repository.specification;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

public class AccommodationSpecification {
    public static Specification<AccommodationModel> getAccommodations(AccommodationParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (params.getHostId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("hostId"), params.getHostId()));
            }

            if (!CollectionUtils.isEmpty(params.getIds())) {
                predicates.add(root.get("id").in(params.getIds()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
