package com.fincore.identity.infrastructure.adapter.out.security;

import com.fincore.identity.application.port.out.TokenGenerator;
import com.fincore.identity.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    @Override
    public String generate(User user) {
        return "temporary-token";
    }
}