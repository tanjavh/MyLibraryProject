package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.service.LoanService;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    // ==============================
    // REST endpoint za pozajmicu knjige
    // ==============================
    @PostMapping("/borrow")
    public LoanDto borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        // Dohvati korisnika
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Nepostojeći korisnik"));

        // Kreiraj pozajmicu i vrati DTO
        return loanService.convertToDto(loanService.createLoan(user, bookId));
    }

    // ==============================
    // REST endpoint za vraćanje knjige
    // ==============================
    @PostMapping("/return/{loanId}")
    public LoanDto returnBook(@PathVariable Long loanId) {
        var loan = loanService.getLoanById(loanId)
                .orElseThrow(() -> new RuntimeException("Pozajmica ne postoji"));

        loanService.returnBook(loan);
        return loanService.convertToDto(loan);
    }
}
