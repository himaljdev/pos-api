package com.returns.service.security;
import com.returns.service.enums.Status;
import com.returns.service.model.CashierUser;
import com.returns.service.repository.CashierUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Log4j2
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
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == Status.ACTIVE,
                true,
                true,
                true,
                java.util.Collections.emptyList()
        );
    }
} 