package com.codegym.validations.custom;

import com.codegym.validations.ImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface Image {
    String message() default "File ảnh không hợp lệ (chỉ chấp nhận png, jpg, jpeg, gif và ≤ 2MB)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
