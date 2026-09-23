package com.ghorivo.commerce.identity.domain.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserAccountTest {

    @Test
    void shouldCreateActiveUserWithNormalizedEmail() {
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");

        UserAccount user = UserAccount.create(
                "Mohit Islam",
                "  MOHIT@EXAMPLE.COM  ",
                "encoded-password",
                UserRole.ADMIN,
                createdAt
        );

        assertThat(user.id()).isNotNull();
        assertThat(user.fullName()).isEqualTo("Mohit Islam");
        assertThat(user.email()).isEqualTo("mohit@example.com");
        assertThat(user.role()).isEqualTo(UserRole.ADMIN);
        assertThat(user.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.createdAt()).isEqualTo(createdAt);
        assertThat(user.updatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldRejectBlankEmail() {
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");

        assertThatThrownBy(() -> UserAccount.create(
                "Mohit Islam",
                "   ",
                "encoded-password",
                UserRole.ADMIN,
                createdAt
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email must not be blank");
    }

    @Test
    void shouldRejectNullRole() {
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");

        assertThatThrownBy(() -> UserAccount.create(
                "Mohit Islam",
                "mohit@example.com",
                "encoded-password",
                null,
                createdAt
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("role must not be null");
    }

    @Test
    void shouldDeactivateActiveUser() {
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");
        Instant deactivatedAt = Instant.parse("2026-09-23T11:00:00Z");

        UserAccount user = UserAccount.create(
                "Mohit Islam",
                "mohit@example.com",
                "encoded-password",
                UserRole.STAFF,
                createdAt
        );

        user.deactivate(deactivatedAt);

        assertThat(user.status()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.updatedAt()).isEqualTo(deactivatedAt);
    }


    // Idempotency test
    @Test
    void shouldNotChangeTimestampWhenAlreadyInactive() {
        Instant createdAt = Instant.parse("2026-09-23T10:00:00Z");
        Instant firstChange = Instant.parse("2026-09-23T11:00:00Z");
        Instant retryTime = Instant.parse("2026-09-23T12:00:00Z");

        UserAccount user = UserAccount.create(
                "Mohit Islam",
                "mohit@example.com",
                "encoded-password",
                UserRole.STAFF,
                createdAt
        );

        user.deactivate(firstChange);
        user.deactivate(retryTime);

        assertThat(user.status()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.updatedAt()).isEqualTo(firstChange);
    }
}