package com.ghorivo.commerce.identity.repository;

import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.entity.UserAccount;
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
class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndFindUserByEmail() {
        String email = "repository-"
                + UUID.randomUUID()
                + "@example.com";

        Instant createdAt = Instant.parse(
                "2026-09-23T10:00:00Z"
        );

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

        Optional<UserAccount> result =
                repository.findByEmail(email);

        assertThat(result).isPresent();

        UserAccount savedUser = result.orElseThrow();

        assertThat(savedUser.id()).isEqualTo(user.id());
        assertThat(savedUser.email()).isEqualTo(email);
        assertThat(savedUser.role()).isEqualTo(UserRole.STAFF);
    }
}
