package com.billing.service.service.impl;

import com.billing.service.model.CashierUser;
import com.billing.service.repository.CashierUserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CashierUserCacheService {
    private final CashierUserRepository cashierUserRepository;

    public CashierUserCacheService(CashierUserRepository cashierUserRepository) {
        this.cashierUserRepository = cashierUserRepository;
    }

    @Cacheable(value = "cashierUser", key = "#username")
    public Optional<CashierUser> findByUsername(String username) {
        return cashierUserRepository.findByUsername(username);
    }
} 