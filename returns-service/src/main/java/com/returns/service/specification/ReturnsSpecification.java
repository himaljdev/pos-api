package com.returns.service.specification;

import com.returns.service.dto.search.KeywordSearch;
import com.returns.service.model.CashierUser;
import com.returns.service.model.Returns;
import com.returns.service.util.DateTimeUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ReturnsSpecification {

    public static Specification<Returns> getSpecification(KeywordSearch filterDto,String username) {

        log.info("Returns filter: " + filterDto);
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Returns, CashierUser> cashierUserJoin = root.join("cashierUser", JoinType.LEFT);
            if (filterDto.getKeyword() != null && !filterDto.getKeyword().isEmpty()) {

                String keyword = "%" + filterDto.getKeyword().toLowerCase() + "%";

                Predicate codePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("returnsInvoice")), keyword);
                Predicate payPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("debitAmount")), keyword);
                Predicate totalPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("totalAmount")), keyword);

                predicates.add(criteriaBuilder.or(codePredicate, payPredicate,totalPredicate));
            }

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),  DateTimeUtil.getStartOfToday()));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"),DateTimeUtil.getEndOfToday()));
            predicates.add(criteriaBuilder.equal(cashierUserJoin.get("username"),username));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Returns> getSpecification(String username) {
        log.info("Returns filter default :");
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Returns, CashierUser> cashierUserJoin = root.join("cashierUser", JoinType.LEFT);
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"),  DateTimeUtil.getStartOfToday()));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"),DateTimeUtil.getEndOfToday()));
            predicates.add(criteriaBuilder.equal(cashierUserJoin.get("username"),username));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
