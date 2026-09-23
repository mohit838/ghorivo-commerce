package com.ghorivo.commerce.identity.infrastructure.persistence;

import com.ghorivo.commerce.identity.domain.user.UserAccount;
import com.ghorivo.commerce.identity.domain.user.UserAccountRepository;
import com.ghorivo.commerce.identity.domain.user.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaUserAccountRepositoryAdapterTest {

    @Autowired
    private UserAccountRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindUserByEmail() {
        String email = "repository-" + UUID.randomUUID() + "@example.com";
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");

        UserAccount user = UserAccount.create(
                "Repository Test User",
                email,
                "encoded-password",
                UserRole.STAFF,
                createdAt
        );

        repository.save(user);

        entityManager.flush();
        entityManager.clear();

        Optional<UserAccount> result = repository.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().id()).isEqualTo(user.id());
        assertThat(result.orElseThrow().email()).isEqualTo(email);
        assertThat(result.orElseThrow().role()).isEqualTo(UserRole.STAFF);
    }
}
