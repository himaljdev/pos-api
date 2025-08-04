package com.billing.service.specification;

import com.billing.service.dto.search.KeywordSearch;
import com.billing.service.enums.Status;
import com.billing.service.model.Customer;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CustomerSpecification {

    public static Specification<Customer> getSpecification(KeywordSearch filterDto) {

        log.info("Customer filter: " + filterDto);

        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.getKeyword() != null && !filterDto.getKeyword().isEmpty()) {

                String keyword = "%" + filterDto.getKeyword().toLowerCase() + "%";

                Predicate firstPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), keyword);
                Predicate lastPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), keyword);

                predicates.add(criteriaBuilder.or(firstPredicate, lastPredicate));
            }

            predicates.add(criteriaBuilder.notEqual(root.get("status"), Status.DELETE));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Customer> getSpecification() {
        log.info("Customer filter default :");
        return (root, query,criteriaBuilder) -> {
             List<Predicate> predicates = new ArrayList<>();
             predicates.add(criteriaBuilder.notEqual(root.get("status"), Status.DELETE));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
