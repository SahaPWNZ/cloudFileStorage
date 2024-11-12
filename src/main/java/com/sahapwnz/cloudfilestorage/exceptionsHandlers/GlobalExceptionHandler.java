package com.sahapwnz.cloudfilestorage.exceptionsHandlers;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.exception.ExistException;
import com.sahapwnz.cloudfilestorage.exception.InvalidNameException;
import com.sahapwnz.cloudfilestorage.exception.RegistrationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "error";
    }

    @ExceptionHandler(RegistrationException.class)
    public String handleRegistrationExceptions(RegistrationException ex, Model model) {

        model.addAttribute("userRequestDTO", new UserRequestDTO());
        model.addAttribute("error", ex.getMessage());
        log.info(ex.getMessage());
        return "/register";
    }

    @ExceptionHandler({InvalidNameException.class,ExistException.class})
    public String handleHomePageExceptions(RuntimeException ex,
                                           RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        log.info(ex.getMessage());
        return "redirect:" + request.getHeader("Referer");
    }

//    @ExceptionHandler(ExistException.class)
//    public String handleRenameExceptions(ExistException ex,
//                                         RedirectAttributes redirectAttributes,
//                                         HttpServletRequest request) {
//        redirectAttributes.addFlashAttribute("error", ex.getMessage());
//        log.info(ex.getMessage());
//        return "redirect:" + request.getHeader("Referer");
//    }
    //     Обработка других исключений (по желанию)
//    @ExceptionHandler(RuntimeException.class)
//    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
//        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка: " + ex.getMessage());
//        model.addAttribute("redirectUrl", request.getRequestURI());
//        return "error"; // Возвращаем имя шаблона для страницы ошибки
//    }
}
