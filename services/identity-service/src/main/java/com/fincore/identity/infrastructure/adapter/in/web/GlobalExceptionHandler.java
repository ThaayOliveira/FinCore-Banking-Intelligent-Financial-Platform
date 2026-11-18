package com.fincore.identity.infrastructure.adapter.in.web;

import com.fincore.identity.domain.exception.UserAlreadyExistsException;
import com.fincore.identity.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExists(
            UserAlreadyExistsException exception
    ) {
        return new ErrorResponse(
                "USER_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(
            UserNotFoundException exception
    ) {
        return new ErrorResponse(
                "USER_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .orElse("Invalid request");

        return new ErrorResponse(
                "VALIDATION_ERROR",
                message
        );
    }

    public record ErrorResponse(
            String code,
            String message
    ) {
    }
}