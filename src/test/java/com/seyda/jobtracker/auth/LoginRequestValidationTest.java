package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.auth.dto.LoginRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestValidationTest {

    private static jakarta.validation.ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void rejectsBlankEmail() {
        var errors = validator.validate(new LoginRequest("   ", "password123"));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("email")));
    }

    @Test
    void rejectsMalformedEmail() {
        var errors = validator.validate(new LoginRequest("not-an-email", "password123"));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("email")));
    }

    @Test
    void rejectsBlankPassword() {
        var errors = validator.validate(new LoginRequest("user@example.com", "   "));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("password")));
    }

    @Test
    void acceptsValidCredentialsShape() {
        var errors = validator.validate(new LoginRequest("user@example.com", "password123"));
        assertTrue(errors.isEmpty());
    }
}
