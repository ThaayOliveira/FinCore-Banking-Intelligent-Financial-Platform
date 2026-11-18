package com.fincore.identity.application.port.in;

import com.fincore.identity.domain.model.User;

public interface CreateUserUseCase {

    User execute(CreateUserCommand command);

    record CreateUserCommand(
            String username,
            String email,
            String password
    ) {
    }
}