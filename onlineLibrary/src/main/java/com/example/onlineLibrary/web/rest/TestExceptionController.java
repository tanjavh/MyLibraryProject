package com.example.onlineLibrary.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestExceptionController {

    @GetMapping("/test/runtime")
    public void throwRuntime() {
        // Ovo će baciti RuntimeException koju hvata RestExceptionHandler
        throw new RuntimeException("Nešto je pošlo po zlu");
    }
}
