package com.ghorivo.commerce.identity.dto.request;

import com.ghorivo.commerce.identity.constants.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(

        @NotBlank(message = "Full name is required")
        @Size(
                max = 120,
                message = "Full name must not exceed 120 characters"
        )
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(
                max = 254,
                message = "Email must not exceed 254 characters"
        )
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 64,
                message = "Password must contain 8 to 64 characters"
        )
        String rawPassword,

        @NotNull(message = "Role is required")
        UserRole role
) {
}
