package com.ghorivo.commerce.identity.service.impl;

import com.ghorivo.commerce.identity.dto.request.CreateUserRequestDto;
import com.ghorivo.commerce.identity.entity.UserAccount;
import com.ghorivo.commerce.identity.exception.DuplicateUserEmailException;
import com.ghorivo.commerce.identity.repository.UserAccountRepository;
import com.ghorivo.commerce.identity.security.PasswordHasher;
import com.ghorivo.commerce.identity.service.UserService;
import com.ghorivo.commerce.identity.util.EmailNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

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
    @Transactional
    public UserAccount create(CreateUserRequestDto request) {
        Objects.requireNonNull(request, "request must not be null");

        if (request.rawPassword() == null
                || request.rawPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "rawPassword must not be blank"
            );
        }

        String normalizedEmail = EmailNormalizer.normalize(
                request.email()
        );

        if (repository.existsByEmail(normalizedEmail)) {
            throw new DuplicateUserEmailException(normalizedEmail);
        }

        String passwordHash = passwordHasher.hash(
                request.rawPassword()
        );

        UserAccount user = UserAccount.create(
                request.fullName(),
                normalizedEmail,
                passwordHash,
                request.role(),
                clock.instant()
        );

        return repository.save(user);
    }
}
