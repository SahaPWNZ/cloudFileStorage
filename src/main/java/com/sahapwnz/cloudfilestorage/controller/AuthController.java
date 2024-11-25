package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.service.BreadcrumbsService;
import com.sahapwnz.cloudfilestorage.service.FileService;
import com.sahapwnz.cloudfilestorage.service.UserDetailsImpl;
import com.sahapwnz.cloudfilestorage.service.UserService;
import com.sahapwnz.cloudfilestorage.util.ControllerUtil;
import com.sahapwnz.cloudfilestorage.util.ValidationUtil;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;
    private final FileService fileService;
    private final BreadcrumbsService breadcrumbsService;

    public AuthController(UserService userService, FileService fileService, BreadcrumbsService breadcrumbsService) {
        this.userService = userService;
        this.fileService = fileService;
        this.breadcrumbsService = breadcrumbsService;
    }

    @GetMapping("/")
    public String home(Model model,
                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                       @RequestParam(required = false) String path) {

        String rootPath = ControllerUtil.getRootPath(userDetails);
        if (path != null) {
            ValidationUtil.isValidPathParametr(path, rootPath, fileService);
        }

        model.addAttribute("login", userDetails.getUsername());
        model.addAttribute("breadcrumbs", breadcrumbsService.getBreadcrumbsForPath(path));
        model.addAttribute("prefix", path != null ? "/" + path : "");
        model.addAttribute("allPath", fileService.getAllPathInThisFolder(rootPath + (path != null ? "/" + path : "")));
        return "home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "register"; // Возвращает имя шаблона для страницы регистрации (register.html)
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRequestDTO userRequestDTO) {
        User user = userService.convertToUser(userRequestDTO);

        userService.saveUser(user);
        fileService.createRootFolder(user.getId());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
