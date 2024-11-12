package com.sahapwnz.cloudfilestorage.util;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.exception.RegistrationException;
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
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new RegistrationException("Введенные пароли не совпадают");
        }
        return true;
    }
}
