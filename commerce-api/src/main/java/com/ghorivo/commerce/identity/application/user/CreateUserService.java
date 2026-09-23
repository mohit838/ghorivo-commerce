package com.ghorivo.commerce.identity.application.user;

import com.ghorivo.commerce.identity.application.security.PasswordHasher;
import com.ghorivo.commerce.identity.domain.user.UserAccount;
import com.ghorivo.commerce.identity.domain.user.UserAccountRepository;

import java.time.Clock;
import java.util.Objects;

public final class CreateUserService {

    private final UserAccountRepository repository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public CreateUserService(
            UserAccountRepository repository,
            PasswordHasher passwordHasher,
            Clock clock
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.passwordHasher = Objects.requireNonNull(passwordHasher);
        this.clock = Objects.requireNonNull(clock);
    }

    public UserAccount create(CreateUserCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (command.rawPassword() == null || command.rawPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "rawPassword must not be blank"
            );
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());

        UserAccount user = UserAccount.create(
                command.fullName(),
                command.email(),
                passwordHash,
                command.role(),
                clock.instant()
        );

        if (repository.existsByEmail(user.email())) {
            throw new DuplicateUserEmailException(user.email());
        }

        return repository.save(user);
    }
}
