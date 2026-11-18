package com.fincore.identity.application.port.out;

import com.fincore.identity.domain.model.User;

public interface TokenGenerator {

    String generate(User user);
}