package com.sahapwnz.cloudfilestorage.util;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserRequestDTO user = (UserRequestDTO) obj;
        return user.getPassword().equals(user.getConfirmPassword());
    }
}
