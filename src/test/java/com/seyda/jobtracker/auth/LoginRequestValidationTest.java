package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.auth.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginRequestValidationTest {

    private final Validator validator = new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();

    @Test
    void rejectsBlankEmail() {
        LoginRequest request = new LoginRequest("   ", "password123");
        var factory = (org.springframework.validation.beanvalidation.LocalValidatorFactoryBean) validator;
        factory.afterPropertiesSet();
        var errors = new BeanPropertyBindingResult(request, "loginRequest");

        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("email"));
    }

    @Test
    void rejectsMalformedEmail() {
        LoginRequest request = new LoginRequest("not-an-email", "password123");
        var factory = (org.springframework.validation.beanvalidation.LocalValidatorFactoryBean) validator;
        factory.afterPropertiesSet();
        var errors = new BeanPropertyBindingResult(request, "loginRequest");

        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("email"));
    }

    @Test
    void rejectsBlankPassword() {
        LoginRequest request = new LoginRequest("user@example.com", "   ");
        var factory = (org.springframework.validation.beanvalidation.LocalValidatorFactoryBean) validator;
        factory.afterPropertiesSet();
        var errors = new BeanPropertyBindingResult(request, "loginRequest");

        validator.validate(request, errors);

        assertTrue(errors.hasFieldErrors("password"));
    }

    @Test
    void acceptsValidCredentialsShape() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        var factory = (org.springframework.validation.beanvalidation.LocalValidatorFactoryBean) validator;
        factory.afterPropertiesSet();
        var errors = new BeanPropertyBindingResult(request, "loginRequest");

        validator.validate(request, errors);

        assertFalse(errors.hasErrors());
    }
}
