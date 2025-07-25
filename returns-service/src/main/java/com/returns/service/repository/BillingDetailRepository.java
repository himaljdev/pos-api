package com.returns.service.repository;

import com.returns.service.model.BillingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long> {
} 