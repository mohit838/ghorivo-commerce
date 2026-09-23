package com.ghorivo.commerce.identity.application.user;

import com.ghorivo.commerce.identity.domain.user.UserRole;

public record CreateUserCommand(
        String fullName,
        String email,
        String rawPassword,
        UserRole role
) {
}
