package huy.project.rating_service.infrastructure.repository.specification;

import huy.project.rating_service.core.domain.dto.request.PendingReviewParams;
import huy.project.rating_service.infrastructure.repository.model.PendingReviewModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class PendingReviewSpecification {
    public static Specification<PendingReviewModel> getPendingReviews(PendingReviewParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (params.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), params.getUserId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
