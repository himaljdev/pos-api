package com.billing.service.repository;

import com.billing.service.model.BillingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDetailRepository extends JpaRepository<BillingDetail, Long> {
} 