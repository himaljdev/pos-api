package com.returns.service.repository;

import com.returns.service.model.ReturnDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnsDetailRepository extends JpaRepository<ReturnDetails,Long> {
    java.util.List<ReturnDetails> findAllByReturns_Billing_Id(Long billingId);
}
