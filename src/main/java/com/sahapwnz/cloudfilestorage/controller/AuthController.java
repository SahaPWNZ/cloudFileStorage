package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import com.sahapwnz.cloudfilestorage.service.UserService;
import io.minio.errors.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
public class AuthController {
    //глянуть отличия принципла и юзерДетаилс
    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public AuthController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String rootPath = "user-" + userDetails.getUser().getId() + "-files";
        model.addAttribute("login", userDetails.getUsername());

        System.out.println(path);
        System.out.println(rootPath);
        if (path != null) {
            model.addAttribute("allPath", fileService.getInfoForThisFolder(rootPath + "/" + path));
        } else {

            model.addAttribute("allPath", fileService.getInfoForThisFolder(rootPath));
        }

        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "register"; // Возвращает имя шаблона для страницы регистрации (register.html)
    }

    @PostMapping("/register")
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
