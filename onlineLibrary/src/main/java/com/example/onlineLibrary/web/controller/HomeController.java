package com.example.onlineLibrary.web.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // ==============================
    // Početna stranica - landing page
    // ==============================

    @GetMapping({"/", "/home"})
    public String home() {
        return "home"; // Thymeleaf template home.html
    }

    // ==============================
    // Dashboard nakon logovanja
    // ==============================
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        // Dobija trenutno ulogovanog korisnika iz Spring Security-a
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "home"; // templates/home.html
    }
}

