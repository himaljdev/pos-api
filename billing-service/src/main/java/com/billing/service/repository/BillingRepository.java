package com.billing.service.repository;

import com.billing.service.model.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> , JpaSpecificationExecutor<Billing> {
    Billing findTopByCashierUser_UsernameOrderByCreatedDateDesc(String username);
} 