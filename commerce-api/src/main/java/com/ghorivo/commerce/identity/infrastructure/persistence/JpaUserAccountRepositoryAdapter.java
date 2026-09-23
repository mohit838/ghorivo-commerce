package com.ghorivo.commerce.identity.infrastructure.persistence;

import com.ghorivo.commerce.identity.domain.user.UserAccount;
import com.ghorivo.commerce.identity.domain.user.UserAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserAccountRepositoryAdapter
        implements UserAccountRepository {

    private final SpringDataUserAccountRepository repository;

    public JpaUserAccountRepositoryAdapter(
            SpringDataUserAccountRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        return repository.save(userAccount);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<UserAccount> findByEmail(String normalizedEmail) {
        return repository.findByEmail(normalizedEmail);
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return repository.existsByEmail(normalizedEmail);
    }
}
