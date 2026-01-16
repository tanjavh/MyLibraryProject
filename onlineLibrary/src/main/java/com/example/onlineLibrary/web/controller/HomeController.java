package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService; // opcionalno, ako želiš više info o korisniku


    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "home";
    }
}
