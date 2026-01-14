package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.service.LoanService;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;
    private final RestTemplate restTemplate;

    private final String libraryUrl = "http://localhost:8081/api/books";

    // =============================
    // Moj pregled pozajmica (korisnik)
    // =============================
    @GetMapping("/active")
    public String getActiveLoans(Model model, Principal principal) {
        String username = principal.getName(); // trenutno ulogovani korisnik
        List<LoanDto> loans = loanService.getActiveLoansByUser(username);
        model.addAttribute("loans", loans);
        return "loans-active"; // Thymeleaf view
    }

    // =============================
    // Pregled svih pozajmica (admin)
    // =============================
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllLoans(Model model) {
        List<LoanDto> loans = loanService.getAllLoansDto();
        model.addAttribute("loans", loans);
        return "loans-all";
    }

    // =============================
    // Forma za kreiranje nove pozajmice (admin)
    // =============================
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createLoanForm(Model model) {
        // Lista korisnika iz lokalne baze
        model.addAttribute("users", userService.getAllUsers());

        // Lista knjiga iz LibraryMicroservice preko REST
        BookInfoResponse[] books = restTemplate.getForObject(libraryUrl, BookInfoResponse[].class);
        model.addAttribute("books", books != null ? List.of(books) : List.of());

        return "loans-create"; // Thymeleaf view
    }

    // =============================
    // Kreiranje nove pozajmice (admin)
    // =============================
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createLoan(@RequestParam Long userId, @RequestParam Long bookId) {
        loanService.createLoanByIds(userId, bookId);
        return "redirect:/loans/all";
    }

    // =============================
    // Vrati knjigu (korisnik)
    // =============================
    @PostMapping("/return/{loanId}")
    public String returnLoan(@PathVariable Long loanId) {
        loanService.returnLoan(loanId);
        return "redirect:/loans/active";
    }
}
