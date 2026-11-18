package com.fincore.identity.application.service;

import com.fincore.identity.application.port.in.CreateUserUseCase.CreateUserCommand;
import com.fincore.identity.application.port.out.PasswordHasher;
import com.fincore.identity.application.port.out.UserRepository;
import com.fincore.identity.domain.exception.UserAlreadyExistsException;
import com.fincore.identity.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private CreateUserService createUserService;

    @BeforeEach
    void setUp() {
        createUserService = new CreateUserService(
                userRepository,
                passwordHasher
        );
    }

    @Test
    void shouldCreateUserSuccessfully() {

        CreateUserCommand command = new CreateUserCommand(
                "thayanne",
                "thayanne@example.com",
                "SenhaTeste123"
        );

        when(userRepository.findByUsername("thayanne"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("thayanne@example.com"))
                .thenReturn(Optional.empty());

        when(passwordHasher.hash("SenhaTeste123"))
                .thenReturn("$2a$10$hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = createUserService.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("thayanne");
        assertThat(result.getEmail()).isEqualTo("thayanne@example.com");
        assertThat(result.getPassword()).isEqualTo("$2a$10$hashed-password");
        assertThat(result.getStatus().name()).isEqualTo("ACTIVE");

        verify(passwordHasher).hash("SenhaTeste123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotCreateUserWhenUsernameAlreadyExists() {

        CreateUserCommand command = new CreateUserCommand(
                "thayanne",
                "thayanne@example.com",
                "SenhaTeste123"
        );

        User existingUser = mock(User.class);

        when(userRepository.findByUsername("thayanne"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> createUserService.execute(command))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Username already exists");

        verify(passwordHasher, never()).hash(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateUserWhenEmailAlreadyExists() {

        CreateUserCommand command = new CreateUserCommand(
                "thayanne",
                "thayanne@example.com",
                "SenhaTeste123"
        );

        when(userRepository.findByUsername("thayanne"))
                .thenReturn(Optional.empty());

        User existingUser = mock(User.class);

        when(userRepository.findByEmail("thayanne@example.com"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> createUserService.execute(command))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");

        verify(passwordHasher, never()).hash(any());
        verify(userRepository, never()).save(any());
    }
}