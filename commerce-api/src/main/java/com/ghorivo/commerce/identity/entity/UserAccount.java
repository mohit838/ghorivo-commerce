package com.ghorivo.commerce.identity.entity;

import com.ghorivo.commerce.identity.constants.UserRole;
import com.ghorivo.commerce.identity.constants.UserStatus;
import com.ghorivo.commerce.identity.util.EmailNormalizer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class UserAccount {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserAccount() {
        // Required by JPA
    }

    private UserAccount(
            UUID id,
            String fullName,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public static UserAccount create(
            String fullName,
            String email,
            String passwordHash,
            UserRole role,
            Instant createdAt
    ) {
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");

        return new UserAccount(
                UUID.randomUUID(),
                requireText(fullName, "fullName"),
                EmailNormalizer.normalize(email),
                requireText(passwordHash, "passwordHash"),
                role,
                UserStatus.ACTIVE,
                createdAt
        );
    }

    public void activate(Instant changedAt) {
        changeStatus(UserStatus.ACTIVE, changedAt);
    }

    public void deactivate(Instant changedAt) {
        changeStatus(UserStatus.INACTIVE, changedAt);
    }

    private void changeStatus(UserStatus newStatus, Instant changedAt) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        Objects.requireNonNull(changedAt, "changedAt must not be null");

        if (changedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException(
                    "changedAt must not be before createdAt"
            );
        }

        if (status == newStatus) {
            return;
        }

        status = newStatus;
        updatedAt = changedAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }

    public UUID id() {
        return id;
    }

    public String fullName() {
        return fullName;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public UserRole role() {
        return role;
    }

    public UserStatus status() {
        return status;
    }

    public long version() {
        return version;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
