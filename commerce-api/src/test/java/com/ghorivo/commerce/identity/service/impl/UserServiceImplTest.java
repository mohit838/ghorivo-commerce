package com.ghorivo.commerce.identity.service.impl;

import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.constants.UserStatus;
import com.ghorivo.commerce.identity.dto.request.CreateUserRequestDto;
import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.exception.DuplicateUserEmailException;
import com.ghorivo.commerce.identity.repository.UserAccountRepository;
import com.ghorivo.commerce.identity.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Instant FIXED_TIME =
            Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private UserAccountRepository repository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                FIXED_TIME,
                ZoneOffset.UTC
        );

        userService = new UserServiceImpl(
                repository,
                passwordHasher,
                fixedClock
        );
    }

    @Test
    void shouldCreateActiveUser() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "Mohit Islam",
                "  MOHIT@EXAMPLE.COM  ",
                "raw-password",
                UserRole.ADMIN
        );

        when(repository.existsByEmail("mohit@example.com"))
                .thenReturn(false);

        when(passwordHasher.hash("raw-password"))
                .thenReturn("hashed-password");

        when(repository.save(any(UserAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount createdUser = userService.create(request);

        assertThat(createdUser.fullName())
                .isEqualTo("Mohit Islam");

        assertThat(createdUser.email())
                .isEqualTo("mohit@example.com");

        assertThat(createdUser.passwordHash())
                .isEqualTo("hashed-password");

        assertThat(createdUser.role())
                .isEqualTo(UserRole.ADMIN);

        assertThat(createdUser.status())
                .isEqualTo(UserStatus.ACTIVE);

        assertThat(createdUser.createdAt())
                .isEqualTo(FIXED_TIME);

        verify(repository)
                .existsByEmail("mohit@example.com");

        verify(passwordHasher)
                .hash("raw-password");

        verify(repository)
                .save(any(UserAccount.class));
    }

    @Test
    void shouldRejectDuplicateEmailBeforeHashingPassword() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "Mohit Islam",
                "  MOHIT@EXAMPLE.COM  ",
                "raw-password",
                UserRole.STAFF
        );

        when(repository.existsByEmail("mohit@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(DuplicateUserEmailException.class)
                .hasMessage(
                        "User already exists with email: "
                                + "mohit@example.com"
                );

        verify(repository)
                .existsByEmail("mohit@example.com");

        verifyNoInteractions(passwordHasher);

        verify(repository, never())
                .save(any(UserAccount.class));
    }
}
