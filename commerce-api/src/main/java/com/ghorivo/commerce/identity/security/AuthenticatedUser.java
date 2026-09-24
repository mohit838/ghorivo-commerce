package com.ghorivo.commerce.identity.security;

import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.constants.UserStatus;
import com.ghorivo.commerce.identity.entity.UserAccount;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NullMarked
public record AuthenticatedUser(
        UUID id,
        String email,
        String passwordHash,
        UserRole role,
        UserStatus status
) implements UserDetails {

    public static AuthenticatedUser from(UserAccount user) {
        return new AuthenticatedUser(
                user.id(),
                user.email(),
                user.passwordHash(),
                user.role(),
                user.status()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(
                "ROLE_" + role.name()
        ));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
