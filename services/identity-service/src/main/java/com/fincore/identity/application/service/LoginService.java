package com.fincore.identity.application.service;

import com.fincore.identity.application.port.in.LoginUseCase;
import com.fincore.identity.application.port.out.PasswordHasher;
import com.fincore.identity.application.port.out.TokenGenerator;
import com.fincore.identity.application.port.out.UserRepository;
import com.fincore.identity.domain.exception.InvalidCredentialsException;
import com.fincore.identity.domain.model.User;
import com.fincore.identity.domain.model.UserStatus;

public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;

    public LoginService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenGenerator tokenGenerator
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public LoginResult execute(LoginCommand command) {

        User user = userRepository
                .findByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        if (!passwordHasher.matches(
                command.password(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenGenerator.generate(user);

        return new LoginResult(accessToken);
    }
}