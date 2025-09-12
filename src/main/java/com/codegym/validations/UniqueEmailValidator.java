package com.codegym.validations;

import com.codegym.repositories.UserRepository;
import com.codegym.validations.custom.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository; // Assuming a Spring Data JPA repository
    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; // Handle null/empty emails based on your application's requirements
        }
        return !userRepository.existsByEmail(email); // Custom method in your repository
    }
}
