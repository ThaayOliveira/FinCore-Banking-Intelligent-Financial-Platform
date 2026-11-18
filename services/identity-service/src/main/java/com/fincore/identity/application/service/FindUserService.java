package com.fincore.identity.application.service;

import com.fincore.identity.application.port.in.FindUserUseCase;
import com.fincore.identity.application.port.out.UserRepository;
import com.fincore.identity.domain.exception.UserNotFoundException;
import com.fincore.identity.domain.model.User;

import java.util.UUID;

public class FindUserService implements FindUserUseCase {

    private final UserRepository userRepository;

    public FindUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(UUID id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}