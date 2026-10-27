package com.sena.Backend_OneLanguage.users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserRequestDtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void acceptsNamesWithSpanishLettersAndValidEmailProviders() {
        UserRequestDto request = validRequest("  María José Muñoz  ", "  maria_jose@outlook.com  ");

        assertTrue(validator.validate(request).isEmpty());
        assertEquals("María José Muñoz", request.getFullName());
        assertEquals("maria_jose@outlook.com", request.getEmail());
    }

    @Test
    void acceptsValidEmailProviders() {
        for (String email : new String[]{"johan@gmail.com", "johan@outlook.com", "johan@hotmail.com", "johan@yahoo.com"}) {
            assertTrue(validator.validate(validRequest("Johan Alexander", email)).isEmpty());
        }
    }

    @Test
    void rejectsInvalidNames() {
        for (String fullName : new String[]{"Johan123", "12345", "!!!", "   "}) {
            assertTrue(validator.validate(validRequest(fullName, "user@example.com")).stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals("fullName")));
        }
    }

    @Test
    void rejectsMalformedEmails() {
        for (String email : new String[]{"Prueba12345@gail.com", "johan@gmail", "@gmail.com", "johan@@gmail.com", "johan@gmail.c", "johan @gmail.com", "johan..acero@gmail.com", "johan@gmail..com", "johan@"}) {
            assertTrue(validator.validate(validRequest("Johan Alexander", email)).stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
        }
    }

    private UserRequestDto validRequest(String fullName, String email) {
        UserRequestDto request = new UserRequestDto();
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPassword("Strong123");
        return request;
    }
}
