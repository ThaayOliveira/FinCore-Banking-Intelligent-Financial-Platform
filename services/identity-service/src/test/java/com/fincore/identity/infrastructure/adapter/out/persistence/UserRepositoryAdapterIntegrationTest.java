package com.fincore.identity.infrastructure.adapter.out.persistence;

import com.fincore.identity.domain.model.User;
import com.fincore.identity.domain.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class UserRepositoryAdapterIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18");

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void shouldSaveAndFindUserById() {

        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = createUser(
                userId,
                "integration-user-id",
                "integration-id@example.com",
                now
        );

        User savedUser = userRepositoryAdapter.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(userId);
        assertThat(savedUser.getUsername())
                .isEqualTo("integration-user-id");
        assertThat(savedUser.getEmail())
                .isEqualTo("integration-id@example.com");

        User foundUser = userRepositoryAdapter
                .findById(userId)
                .orElseThrow();

        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername())
                .isEqualTo("integration-user-id");
        assertThat(foundUser.getEmail())
                .isEqualTo("integration-id@example.com");
        assertThat(foundUser.getStatus())
                .isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldFindUserByUsername() {

        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = createUser(
                userId,
                "username-search",
                "username-search@example.com",
                now
        );

        userRepositoryAdapter.save(user);

        User foundUser = userRepositoryAdapter
                .findByUsername("username-search")
                .orElseThrow();

        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername())
                .isEqualTo("username-search");
        assertThat(foundUser.getEmail())
                .isEqualTo("username-search@example.com");
    }

    @Test
    void shouldFindUserByEmail() {

        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = createUser(
                userId,
                "email-search",
                "email-search@example.com",
                now
        );

        userRepositoryAdapter.save(user);

        User foundUser = userRepositoryAdapter
                .findByEmail("email-search@example.com")
                .orElseThrow();

        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername())
                .isEqualTo("email-search");
        assertThat(foundUser.getEmail())
                .isEqualTo("email-search@example.com");
    }

    private User createUser(
            UUID id,
            String username,
            String email,
            OffsetDateTime now
    ) {
        return new User(
                id,
                username,
                email,
                "$2a$10$integration-test-hash",
                UserStatus.ACTIVE,
                now,
                now
        );
    }
}