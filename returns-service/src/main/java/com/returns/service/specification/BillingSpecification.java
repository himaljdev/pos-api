package com.returns.service.specification;
import com.returns.service.dto.request.CustomerReturnSearchDTO;
import com.returns.service.dto.search.KeywordSearch;
import com.returns.service.enums.Status;
import com.returns.service.model.Billing;
import com.returns.service.model.Item;
import com.returns.service.model.Location;
import com.returns.service.model.Stock;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class BillingSpecification {
    public static Specification<Billing> getSpecification(CustomerReturnSearchDTO filterDto) {
        log.info("Stock filter: " + filterDto);
        return (root, query,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.getInvoiceNo() != null && !filterDto.getInvoiceNo().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("invoiceNumber")), "%" + filterDto.getInvoiceNo().toLowerCase() + "%"));
            }

            if (filterDto.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), filterDto.getFromDate()));
            }

            if (filterDto.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), filterDto.getToDate()));
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
