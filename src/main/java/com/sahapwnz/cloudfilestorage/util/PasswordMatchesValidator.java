package com.sahapwnz.cloudfilestorage.util;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.exception.ValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserRequestDTO user = (UserRequestDTO) obj;
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new ValidationException("Введенные пароли не совпадают", HttpStatus.BAD_REQUEST);
        }
        return true;
    }
}
