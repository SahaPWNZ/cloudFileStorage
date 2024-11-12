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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception ex) {
        ModelAndView mav = new ModelAndView("error"); // Имя вашей страницы
        mav.addObject("errorMessage", ex.getMessage());
//        mav.addObject("errorCode", 500);
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        model.addAttribute("error", errors);
        log.info(ex.getMessage());
        return "/register";
    }

    @ExceptionHandler(RegistrationException.class)
    public String handleRegistrationExceptions(RegistrationException ex, Model model) {

        model.addAttribute("userRequestDTO", new UserRequestDTO());
        model.addAttribute("error", ex.getMessage());
        log.info(ex.getMessage());
        return "/register";
    }

    @ExceptionHandler({InvalidNameException.class, ExistException.class})
    public String handleHomePageExceptions(RuntimeException ex,
                                           RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        log.info(ex.getMessage());
        return "redirect:" + request.getHeader("Referer");
    }
}
