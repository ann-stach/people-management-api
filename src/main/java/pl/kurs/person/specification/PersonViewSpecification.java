package pl.kurs.person.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.kurs.person.model.query.FindPersonQuery;
import pl.kurs.person.model.views.PersonView;

import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class PersonViewSpecification implements Specification<PersonView> {

    private final FindPersonQuery findPersonQuery;
    @Override
    public Predicate toPredicate(Root<PersonView> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return findPersonQuery.getCriteria().stream()
                .map(criteria -> createPredicateForCriteria(root, query, cb, criteria))
                .filter(Objects::nonNull)
                .reduce(cb::and)
                .orElse(cb.conjunction());
    }

    private Predicate createPredicateForCriteria(Root<PersonView> root, CriteriaQuery<?> query, CriteriaBuilder cb, FindPersonQuery.QueryCriteria criteria) {
        String key = criteria.getKey();
        Object value = criteria.getValue();
        String operation = criteria.getOperation();

        if (value == null) {
            return null;
        }

        switch (operation) {
            case "containsIgnoreCase" -> {
                return cb.like(cb.lower(root.get(key)), "%" + value.toString().toLowerCase() + "%");
            }
            case "range" -> {
                if (value instanceof List<?> range && range.size() == 2) {
                    Comparable<?> start = (Comparable<?>) range.get(0);
                    Comparable<?> end = (Comparable<?>) range.get(1);

                    return cb.between(root.get(key), (Comparable) start, (Comparable) end);
                } else {
                    throw new IllegalArgumentException("Invalid range criteria for key: " + key);
                }
            }
            default -> throw new UnsupportedOperationException("Operation not supported: " + operation);
        }
    }

}
