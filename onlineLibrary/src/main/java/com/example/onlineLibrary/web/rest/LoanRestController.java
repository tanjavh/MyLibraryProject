package com.example.onlineLibrary.web.rest;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.service.LoanService;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanRestController {

    private final LoanService loanService;
    private final UserService userService;

    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestParam Long userId,
                                             @RequestParam Long bookId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));

        try {
            loanService.createLoan(user, bookId);
            return ResponseEntity.ok("Knjiga je pozajmljena!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestParam Long loanId) {
        Loan loan = loanService.getLoanById(loanId)
                .orElseThrow(() -> new RuntimeException("Pozajmica ne postoji"));

        loanService.returnBook(loan);
        return ResponseEntity.ok("Knjiga je vraćena!");
    }
}

