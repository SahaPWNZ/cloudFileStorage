package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import com.sahapwnz.cloudfilestorage.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {
    //глянуть отличия принципла и юзерДетаилс
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println("---------------");
        System.out.println(principal.getName());
        System.out.println(userDetails);

        model.addAttribute("login", userDetails.getUsername());
        return "home"; // Убедитесь, что у вас есть шаблон home.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "register"; // Возвращает имя шаблона для страницы регистрации (register.html)
    }

    @PostMapping("/register")
//    public String registerUser(@RequestParam String login, @RequestParam String password) {
    public String registerUser(@Valid @ModelAttribute UserRequestDTO userRequestDTO) {

        User user = new User();
        user.setLogin(userRequestDTO.getLogin());
        user.setPassword(userRequestDTO.getPassword());

        if (userService.saveUser(user)) {
            return "redirect:/login"; // Перенаправление на страницу входа после успешной регистрации
        } else {
            return "redirect:/register?error"; // Перенаправление обратно на страницу регистрации с ошибкой
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Возвращает имя шаблона для страницы входа (login.html)
    }
}
