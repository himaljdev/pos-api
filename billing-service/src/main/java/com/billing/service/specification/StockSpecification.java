package com.billing.service.specification;

import com.billing.service.enums.Status;
import com.billing.service.model.Location;
import com.billing.service.model.Stock;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class StockSpecification {

    public static Specification<Stock> getSpecification(String location) {
        log.info("Stock filter default :");
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Stock, Location> locationJoin = root.join("location", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(root.get("status"), Status.ACTIVE));
            predicates.add(criteriaBuilder.equal(locationJoin.get("code"), location));
            predicates.add(criteriaBuilder.notEqual(root.get("qty"), 0));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

}
