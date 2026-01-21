package com.example.onlineLibrary.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class UserRegisterDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .username("user")
                .email("invalid-email")
                .password("password")
                .confirmPassword("password")
                .build();

        var violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void whenUsernameIsNull_thenValidationFails() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .email("test@example.com")
                .password("password")
                .confirmPassword("password")
                .build();

        var violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void whenEverythingValid_thenNoViolations() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .username("user")
                .email("test@example.com")
                .password("password")
                .confirmPassword("password")
                .build();

        var violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}

