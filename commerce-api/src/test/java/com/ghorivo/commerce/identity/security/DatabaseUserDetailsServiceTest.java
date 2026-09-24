package com.ghorivo.commerce.identity.security;

import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    private static final Instant CREATED_AT =
            Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private UserAccountRepository repository;

    private DatabaseUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService =
                new DatabaseUserDetailsService(repository);
    }

    @Test
    void shouldLoadActiveAdminByNormalizedEmail() {
        UserAccount user = UserAccount.create(
                "First Admin",
                "admin@ghorivo.com",
                "hashed-password",
                UserRole.ADMIN,
                CREATED_AT
        );

        when(repository.findByEmail("admin@ghorivo.com"))
                .thenReturn(Optional.of(user));

        AuthenticatedUser result =
                (AuthenticatedUser) userDetailsService
                        .loadUserByUsername(
                                "  ADMIN@GHORIVO.COM  "
                        );

        assertThat(result.id()).isEqualTo(user.id());
        assertThat(result.getUsername())
                .isEqualTo("admin@ghorivo.com");
        assertThat(result.getPassword())
                .isEqualTo("hashed-password");
        assertThat(result.isEnabled()).isTrue();

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");

        verify(repository)
                .findByEmail("admin@ghorivo.com");
    }

    @Test
    void shouldLoadInactiveUserAsDisabled() {
        UserAccount user = UserAccount.create(
                "Inactive Staff",
                "staff@ghorivo.com",
                "hashed-password",
                UserRole.STAFF,
                CREATED_AT
        );

        user.deactivate(
                Instant.parse("2026-09-24T11:00:00Z")
        );

        when(repository.findByEmail("staff@ghorivo.com"))
                .thenReturn(Optional.of(user));

        AuthenticatedUser result =
                (AuthenticatedUser) userDetailsService
                        .loadUserByUsername(
                                "staff@ghorivo.com"
                        );

        assertThat(result.isEnabled()).isFalse();

        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_STAFF");
    }

    @Test
    void shouldRejectUnknownEmail() {
        when(repository.findByEmail("missing@ghorivo.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService
                .loadUserByUsername("missing@ghorivo.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid email or password");

        verify(repository)
                .findByEmail("missing@ghorivo.com");
    }

    @Test
    void shouldRejectBlankEmailWithoutCallingRepository() {
        assertThatThrownBy(() -> userDetailsService
                .loadUserByUsername("   "))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid email or password");

        verify(repository, never()).findByEmail(
                org.mockito.ArgumentMatchers.anyString()
        );
    }
}
