package com.fincore.identity.infrastructure.adapter.in.web;

import com.fincore.identity.application.port.in.CreateUserUseCase;
import com.fincore.identity.application.port.in.FindUserUseCase;
import com.fincore.identity.domain.exception.UserAlreadyExistsException;
import com.fincore.identity.domain.exception.UserNotFoundException;
import com.fincore.identity.domain.model.User;
import com.fincore.identity.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private FindUserUseCase findUserUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        UserController userController = new UserController(
                createUserUseCase,
                findUserUseCase
        );

        GlobalExceptionHandler exceptionHandler =
                new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void shouldCreateUser() throws Exception {

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

        when(createUserUseCase.execute(any(
                CreateUserUseCase.CreateUserCommand.class
        ))).thenReturn(user);

        String request = """
                {
                    "username": "thayanne",
                    "email": "thayanne@example.com",
                    "password": "SenhaTeste123"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("thayanne"))
                .andExpect(jsonPath("$.email")
                        .value("thayanne@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(createUserUseCase).execute(any(
                CreateUserUseCase.CreateUserCommand.class
        ));
    }

    @Test
    void shouldFindUserById() throws Exception {

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

        when(findUserUseCase.execute(userId))
                .thenReturn(user);

        mockMvc.perform(
                        get("/users/{id}", userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("thayanne"))
                .andExpect(jsonPath("$.email")
                        .value("thayanne@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(findUserUseCase).execute(userId);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {

        UUID userId = UUID.randomUUID();

        when(findUserUseCase.execute(userId))
                .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(
                        get("/users/{id}", userId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("User not found: " + userId));

        verify(findUserUseCase).execute(userId);
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {

        when(createUserUseCase.execute(any(
                CreateUserUseCase.CreateUserCommand.class
        ))).thenThrow(
                new UserAlreadyExistsException(
                        "Username already exists: thayanne"
                )
        );

        String request = """
                {
                    "username": "thayanne",
                    "email": "thayanne@example.com",
                    "password": "SenhaTeste123"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value("USER_ALREADY_EXISTS"));

        verify(createUserUseCase).execute(any(
                CreateUserUseCase.CreateUserCommand.class
        ));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsTooShort()
            throws Exception {

        String request = """
                {
                    "username": "thayanne",
                    "email": "thayanne@example.com",
                    "password": "123"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(createUserUseCase);
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid()
            throws Exception {

        String request = """
                {
                    "username": "thayanne",
                    "email": "email-invalido",
                    "password": "SenhaTeste123"
                }
                """;

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value("VALIDATION_ERROR"));

        verifyNoInteractions(createUserUseCase);
    }
}