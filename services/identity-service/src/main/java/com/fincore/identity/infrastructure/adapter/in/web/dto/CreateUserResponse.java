package com.fincore.identity.infrastructure.adapter.in.web.dto;

import com.fincore.identity.domain.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String username,
        String email,
        String status,
        OffsetDateTime createdAt
) {

    public static CreateUserResponse from(User user) {
        return new CreateUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}