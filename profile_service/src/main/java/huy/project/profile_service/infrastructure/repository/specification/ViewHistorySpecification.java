package huy.project.profile_service.infrastructure.repository.specification;

import huy.project.profile_service.core.domain.dto.request.ViewHistoryParams;
import huy.project.profile_service.infrastructure.repository.model.ViewHistoryModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ViewHistorySpecification {
    public static Specification<ViewHistoryModel> getViewHistories(ViewHistoryParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (params.getTouristId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("touristId"), params.getTouristId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
