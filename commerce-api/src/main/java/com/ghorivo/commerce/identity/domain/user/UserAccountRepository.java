package com.ghorivo.commerce.identity.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {

    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findById(UUID id);

    Optional<UserAccount> findByEmail(String normalizedEmail);

    boolean existsByEmail(String normalizedEmail);
}