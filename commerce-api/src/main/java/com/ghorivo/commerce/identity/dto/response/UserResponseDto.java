package com.ghorivo.commerce.identity.dto.response;

import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.constants.UserStatus;
import com.ghorivo.commerce.identity.entity.UserAccount;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserResponseDto from(UserAccount user) {
        return new UserResponseDto(
                user.id(),
                user.fullName(),
                user.email(),
                user.role(),
                user.status(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
