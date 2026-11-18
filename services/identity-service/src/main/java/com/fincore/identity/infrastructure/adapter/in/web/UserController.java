package com.fincore.identity.infrastructure.adapter.in.web;

import com.fincore.identity.application.port.in.CreateUserUseCase;
import com.fincore.identity.application.port.in.FindUserUseCase;
import com.fincore.identity.infrastructure.adapter.in.web.dto.CreateUserRequest;
import com.fincore.identity.infrastructure.adapter.in.web.dto.CreateUserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserUseCase findUserUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            FindUserUseCase findUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        CreateUserUseCase.CreateUserCommand command =
                new CreateUserUseCase.CreateUserCommand(
                        request.username(),
                        request.email(),
                        request.password()
                );

        var user = createUserUseCase.execute(command);

        return CreateUserResponse.from(user);
    }

    @GetMapping("/{id}")
    public CreateUserResponse findUser(
            @PathVariable UUID id
    ) {
        var user = findUserUseCase.execute(id);

        return CreateUserResponse.from(user);
    }
}