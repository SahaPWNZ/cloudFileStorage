package com.sahapwnz.cloudfilestorage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus statusCode;

    public ApplicationException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}