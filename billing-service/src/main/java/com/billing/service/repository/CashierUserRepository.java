package com.billing.service.repository;

import com.billing.service.model.CashierUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashierUserRepository extends JpaRepository<CashierUser, Long> {
    Optional<CashierUser> findByUsername(String username);
}
