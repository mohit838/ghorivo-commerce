package com.ghorivo.commerce.identity.dto.request;

import com.ghorivo.commerce.identity.constants.UserRole;

public record CreateUserRequestDto(
        String fullName,
        String email,
        String rawPassword,
        UserRole role
) {
}
