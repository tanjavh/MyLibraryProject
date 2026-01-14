package com.example.LibraryMicroservice.web.rest;

import com.example.LibraryMicroservice.model.dto.LoanDto;
import com.example.LibraryMicroservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanRestController {

    private final LoanService loanService;

    @GetMapping("/user/{username}/active")
    public ResponseEntity<List<LoanDto>> getActiveLoansByUser(@PathVariable String username) {
        List<LoanDto> loans = loanService.getActiveLoansByUser(username);
        return ResponseEntity.ok(loans);
    }
    // Pozajmi knjigu
    @PostMapping("/borrow/{username}/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable String username, @PathVariable Long bookId) {
        try {
            loanService.borrowBook(username, bookId);
            return ResponseEntity.ok("Knjiga uspešno pozajmljena!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Vrati knjigu
    @PostMapping("/return/{username}/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable String username, @PathVariable Long bookId) {
        try {
            loanService.returnBook(username, bookId);
            return ResponseEntity.ok("Knjiga uspešno vraćena!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

