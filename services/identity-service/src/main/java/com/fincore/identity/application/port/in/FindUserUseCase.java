package com.fincore.identity.application.port.in;

import com.fincore.identity.domain.model.User;

import java.util.UUID;

public interface FindUserUseCase {

    User execute(UUID id);
}