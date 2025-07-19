package com.auth.service.repository;

import com.auth.service.model.PasswordPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicy, Long> {
    Optional<PasswordPolicy> findTopByOrderByIdAsc();
}
