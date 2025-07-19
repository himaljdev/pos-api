package com.auth.service.security;

import com.auth.service.model.CashierUser;
import com.auth.service.repository.CashierUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CashierUserDetailsService implements UserDetailsService {

    private final CashierUserRepository cashierUserRepository;

    @Autowired
    public CashierUserDetailsService(CashierUserRepository cashierUserRepository) {
        this.cashierUserRepository = cashierUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        CashierUser user = cashierUserRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found or not active: {}", username);
                    return new UsernameNotFoundException("User not found or inactive: " + username);
                });
        log.info("User loaded and active: {}", username);
        return new CashierUserDetails(user);
    }
} 