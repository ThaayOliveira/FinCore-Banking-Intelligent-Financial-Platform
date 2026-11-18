package com.fincore.identity.application.port.in;

public interface LoginUseCase {

    LoginResult execute(LoginCommand command);

    record LoginCommand(
            String username,
            String password
    ) {
    }

    record LoginResult(
            String accessToken
    ) {
    }
}