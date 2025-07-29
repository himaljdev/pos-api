package com.billing.service.specification;

import com.billing.service.dto.search.KeywordSearch;
import com.billing.service.model.Billing;
import com.billing.service.model.Customer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class BillingSpecification {

    public static Specification<Billing> getSpecification(KeywordSearch filterDto) {

        log.info("Billing filter: " + filterDto);

        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Billing, Customer> customerJoin = root.join("customer", JoinType.LEFT);
            if (filterDto.getKeyword() != null && !filterDto.getKeyword().isEmpty()) {

                String keyword = "%" + filterDto.getKeyword().toLowerCase() + "%";

                Predicate codePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("invoiceNumber")), keyword);
                Predicate payPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("payAmount")), keyword);
                Predicate totalPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("totalAmount")), keyword);
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("firstName")), keyword);
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(customerJoin.get("lastName")), keyword);

                predicates.add(criteriaBuilder.or(codePredicate, payPredicate,totalPredicate,firstNamePredicate,lastNamePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Billing> getSpecification() {
        log.info("Billing filter default :");
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
