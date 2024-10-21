package com.sahapwnz.cloudfilestorage.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApplicationException{
    public ValidationException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }
}
