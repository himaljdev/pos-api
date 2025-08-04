package com.billing.service.repository;

import com.billing.service.model.CheckoutToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckoutTokenRepository extends JpaRepository<CheckoutToken, Long> {
    Optional<CheckoutToken> findByToken(String token);
}
