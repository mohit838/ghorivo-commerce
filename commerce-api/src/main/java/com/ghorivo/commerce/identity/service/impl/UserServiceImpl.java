package com.ghorivo.commerce.identity.service.impl;

import com.ghorivo.commerce.identity.dto.request.CreateUserRequestDto;
import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.exception.DuplicateUserEmailException;
import com.ghorivo.commerce.identity.repository.UserAccountRepository;
import com.ghorivo.commerce.identity.security.PasswordHasher;
import com.ghorivo.commerce.identity.service.UserService;

import java.time.Clock;
import java.util.Objects;

public final class UserServiceImpl implements UserService {

    private final UserAccountRepository repository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public UserServiceImpl(
            UserAccountRepository repository,
            PasswordHasher passwordHasher,
            Clock clock
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public UserAccount create(CreateUserRequestDto request) {
        Objects.requireNonNull(request, "request must not be null");

        if (request.rawPassword() == null
                || request.rawPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "rawPassword must not be blank"
            );
        }

        String passwordHash = passwordHasher.hash(
                request.rawPassword()
        );

        UserAccount user = UserAccount.create(
                request.fullName(),
                request.email(),
                passwordHash,
                request.role(),
                clock.instant()
        );

        if (repository.existsByEmail(user.email())) {
            throw new DuplicateUserEmailException(user.email());
        }

        return repository.save(user);
    }
}
