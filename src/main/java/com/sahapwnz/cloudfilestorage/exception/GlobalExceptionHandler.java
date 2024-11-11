package com.sahapwnz.cloudfilestorage.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        model.addAttribute("redirectUrl", request.getRequestURI());
        model.addAttribute("errorMessage", "Ошибка валидации: " + errors);
        return "error"; // Возвращаем имя шаблона для страницы ошибки
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationExceptions2(ValidationException ex, Model model, HttpServletRequest request) {

        model.addAttribute("redirectUrl", request.getRequestURI());
        model.addAttribute("errorMessage", "Ошибка валидации: " +
                ex.getMessage() + "код ошибки: " + ex.getStatusCode());
        return "error"; // Возвращаем имя шаблона для страницы ошибки
    }

    //     Обработка других исключений (по желанию)
//    @ExceptionHandler(RuntimeException.class)
//    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
//        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка: " + ex.getMessage());
//        model.addAttribute("redirectUrl", request.getRequestURI());
//        return "error"; // Возвращаем имя шаблона для страницы ошибки
//    }
}
