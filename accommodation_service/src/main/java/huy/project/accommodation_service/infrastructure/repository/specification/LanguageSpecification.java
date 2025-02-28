package huy.project.accommodation_service.infrastructure.repository.specification;

import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.infrastructure.repository.model.LanguageModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class LanguageSpecification {
    public static Specification<LanguageModel> getLanguages(GetLanguageParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (StringUtils.hasText(params.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + params.getName() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
