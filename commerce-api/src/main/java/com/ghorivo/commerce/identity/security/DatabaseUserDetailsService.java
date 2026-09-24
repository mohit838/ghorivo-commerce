package com.ghorivo.commerce.identity.security;

import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.repository.UserAccountRepository;
import com.ghorivo.commerce.identity.util.EmailNormalizer;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@NullMarked
@Service
public class DatabaseUserDetailsService
        implements UserDetailsService {

    private final UserAccountRepository repository;

    public DatabaseUserDetailsService(
            UserAccountRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        String normalizedEmail;

        try {
            normalizedEmail = EmailNormalizer.normalize(email);
        } catch (IllegalArgumentException exception) {
            throw new UsernameNotFoundException(
                    "Invalid email or password"
            );
        }

        UserAccount user = repository
                .findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Invalid email or password"
                ));

        return AuthenticatedUser.from(user);
    }
}
