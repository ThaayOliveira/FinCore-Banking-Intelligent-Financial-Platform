package com.fincore.identity.infrastructure.config;

import com.fincore.identity.application.port.in.CreateUserUseCase;
import com.fincore.identity.application.port.in.FindUserUseCase;
import com.fincore.identity.application.port.in.LoginUseCase;
import com.fincore.identity.application.port.out.PasswordHasher;
import com.fincore.identity.application.port.out.TokenGenerator;
import com.fincore.identity.application.port.out.UserRepository;
import com.fincore.identity.application.service.CreateUserService;
import com.fincore.identity.application.service.FindUserService;
import com.fincore.identity.application.service.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        return new CreateUserService(
                userRepository,
                passwordHasher
        );
    }

    @Bean
    public FindUserUseCase findUserUseCase(
            UserRepository userRepository
    ) {
        return new FindUserService(
                userRepository
        );
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            TokenGenerator tokenGenerator
    ) {
        return new LoginService(
                userRepository,
                passwordHasher,
                tokenGenerator
        );
    }
}