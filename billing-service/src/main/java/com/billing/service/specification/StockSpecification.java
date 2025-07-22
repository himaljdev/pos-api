package com.billing.service.specification;

import com.billing.service.dto.search.KeywordSearch;
import com.billing.service.enums.Status;
import com.billing.service.model.Item;
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
    public static Specification<Stock> getSpecification(KeywordSearch filterDto, String location) {
        log.info("Stock filter: " + filterDto);
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Stock, Item> itemJoin = root.join("item", JoinType.LEFT);
            Join<Stock, Location> locationJoin = root.join("location", JoinType.LEFT);

            if (filterDto.getKeyword() != null && !filterDto.getKeyword().isEmpty()) {
                String keyword = "%" + filterDto.getKeyword().toLowerCase() + "%";

                Predicate codePredicate = criteriaBuilder.like(criteriaBuilder.lower(itemJoin.get("code")), keyword);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(itemJoin.get("description")), keyword);

                predicates.add(criteriaBuilder.or(codePredicate, namePredicate));
            }

            predicates.add(criteriaBuilder.equal(root.get("status"), Status.ACTIVE));
            predicates.add(criteriaBuilder.equal(locationJoin.get("code"), location));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Stock> getSpecification( String location) {
        log.info("Stock filter default :");
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Stock, Location> locationJoin = root.join("location", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(root.get("status"), Status.ACTIVE));
            predicates.add(criteriaBuilder.equal(locationJoin.get("code"), location));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

}
