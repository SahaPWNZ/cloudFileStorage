package com.sahapwnz.cloudfilestorage.exceptionsHandlers;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.exception.ExistException;
import com.sahapwnz.cloudfilestorage.exception.InvalidNameException;
import com.sahapwnz.cloudfilestorage.exception.RegistrationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception ex) {
        log.info(ex.getMessage());
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        model.addAttribute("error", extractDefaultMessages(ex));
        log.info(ex.getMessage());
        return "register";
    }

    @ExceptionHandler(RegistrationException.class)
    public String handleRegistrationExceptions(RegistrationException ex, Model model) {

        model.addAttribute("userRequestDTO", new UserRequestDTO());
        model.addAttribute("error", ex.getMessage());
        log.info(ex.getMessage());
        return "register";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleLoadExceptions(MaxUploadSizeExceededException ex,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request
    ) {
        log.info(ex.getMessage()+"TOO_Large");
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:" + request.getHeader("Referer");}

    @ExceptionHandler({InvalidNameException.class, ExistException.class})
    public String handleHomePageExceptions(RuntimeException ex,
                                           RedirectAttributes redirectAttributes,
                                           HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        log.info(ex.getMessage()+"3exceptX");
        return "redirect:" + request.getHeader("Referer");
    }

    private String extractDefaultMessages(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> messages = bindingResult.getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        return String.join(", ", messages);
    }
}
