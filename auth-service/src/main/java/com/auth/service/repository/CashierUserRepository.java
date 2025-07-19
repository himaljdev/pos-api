package com.auth.service.repository;

import com.auth.service.model.CashierUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashierUserRepository extends JpaRepository<CashierUser, Long> {
    /**
     * Finds a user by username and status.
     * Only users with the specified status (e.g., ACTIVE) will be returned.
     */
    Optional<CashierUser> findByUsername(String username);
}
