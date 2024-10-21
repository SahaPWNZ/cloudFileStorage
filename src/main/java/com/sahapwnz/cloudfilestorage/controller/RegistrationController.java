package com.sahapwnz.cloudfilestorage.controller;

import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

}

//@Controller
//@RequestMapping("/register")
//public class RegistrationController {
//    private final UserService userService;
//
//    @Autowired
//    public RegistrationController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public String getRegistrationForm(){
//        return "register";
//    }
//
////    @PostMapping
////    public String registerUser(@RequestParam String login, @RequestParam String password){
////        User user = User.builder()
////                .login(login)
////                .password(password)
////                .build();
////        if (userService.saveUser(user)) {
////            return "redirect:/login"; // Перенаправление на страницу входа после успешной регистрации
////        } else {
////            return "redirect:/register?error"; // Перенаправление обратно на страницу регистрации с ошибкой
////        }
////    }
//
//}
