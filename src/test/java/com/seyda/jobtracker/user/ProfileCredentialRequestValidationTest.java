package com.seyda.jobtracker.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

class ProfileCredentialRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void rejectsMalformedNewEmail() {
        var errors = validator.validate(new UpdateEmailRequest("invalid-email", "current123"));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("newEmail")));
    }

    @Test
    void rejectsBlankCurrentPasswordForEmailChange() {
        var errors = validator.validate(new UpdateEmailRequest("user@example.com", " "));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("currentPassword")));
    }

    @Test
    void rejectsShortNewPassword() {
        var errors = validator.validate(new UpdatePasswordRequest("current123", "short"));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("newPassword")));
    }

    @Test
    void rejectsBlankNewPassword() {
        var errors = validator.validate(new UpdatePasswordRequest("current123", " "));
        assertTrue(errors.stream().anyMatch(error -> error.getPropertyPath().toString().equals("newPassword")));
    }
}
