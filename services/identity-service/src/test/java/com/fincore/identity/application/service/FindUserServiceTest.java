package com.fincore.identity.application.service;

import com.fincore.identity.application.port.in.FindUserUseCase;
import com.fincore.identity.application.port.out.UserRepository;
import com.fincore.identity.domain.exception.UserNotFoundException;
import com.fincore.identity.domain.model.User;
import com.fincore.identity.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private FindUserService findUserService;

    @BeforeEach
    void setUp() {
        findUserService = new FindUserService(
                userRepository
        );
    }

    @Test
    void shouldFindUserById() {

        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                userId,
                "thayanne",
                "thayanne@example.com",
                "$2a$10$hashed-password",
                UserStatus.ACTIVE,
                now,
                now
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User result = findUserService.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("thayanne");
        assertThat(result.getEmail())
                .isEqualTo("thayanne@example.com");
        assertThat(result.getStatus())
                .isEqualTo(UserStatus.ACTIVE);

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> findUserService.execute(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found: " + userId);

        verify(userRepository).findById(userId);
    }
}