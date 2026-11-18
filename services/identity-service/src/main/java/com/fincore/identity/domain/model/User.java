package com.fincore.identity.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String username;
    private final String email;
    private final String password;
    private UserStatus status;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User(
            UUID id,
            String username,
            String email,
            String password,
            UserStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = OffsetDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = OffsetDateTime.now();
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
        this.updatedAt = OffsetDateTime.now();
    }
}