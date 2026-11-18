package com.fincore.identity.application.service;

import com.fincore.identity.application.port.in.CreateUserUseCase;
import com.fincore.identity.application.port.out.PasswordHasher;
import com.fincore.identity.application.port.out.UserRepository;
import com.fincore.identity.domain.exception.UserAlreadyExistsException;
import com.fincore.identity.domain.model.User;
import com.fincore.identity.domain.model.UserStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public CreateUserService(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public User execute(CreateUserCommand command) {

        validateUniqueness(command);

        String hashedPassword = passwordHasher.hash(command.password());

        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                UUID.randomUUID(),
                command.username(),
                command.email(),
                hashedPassword,
                UserStatus.ACTIVE,
                now,
                now
        );

        return userRepository.save(user);
    }

    private void validateUniqueness(CreateUserCommand command) {

        if (userRepository.findByUsername(command.username()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Username already exists: " + command.username()
            );
        }

        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email already exists: " + command.email()
            );
        }
    }
}