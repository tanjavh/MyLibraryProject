package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.service.LoanService;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // ==============================
    // Moj pregled pozajmica (korisnik)
    // ==============================
    @GetMapping("/active")
    public String myLoans(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User currentUser = userService.findByUsername(username).orElse(null);
        if (currentUser == null) return "redirect:/login";

        List<LoanDto> loans = loanService.getActiveLoansByUser(username);
        model.addAttribute("loans", loans);
        model.addAttribute("currentUserBlocked", currentUser.isBlocked());

        return "loans-active";
    }

    // ==============================
    // Pregled svih pozajmica (admin)
    // ==============================
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllLoans(Model model) {
        List<LoanDto> loans = loanService.getAllLoansDto();
        model.addAttribute("loans", loans);
        return "loans-all";
    }

    // ==============================
    // Forma za kreiranje nove pozajmice (admin)
    // ==============================
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createLoanForm(Model model) {
        model.addAttribute("users", userService.getAllUsers());

        BookInfoResponse[] books = null;
        try {
            books = restTemplate.getForObject(libraryUrl, BookInfoResponse[].class);
        } catch (Exception e) {
            System.out.println("Ne mogu da dobijem listu knjiga: " + e.getMessage());
        }

        model.addAttribute("books", books != null ? List.of(books) : List.of());
        return "loans-create";
    }

//     ==============================
//     Kreiranje nove pozajmice (admin)
//     ==============================
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createLoan(@RequestParam Long userId, @RequestParam Long bookId) {
        loanService.createLoanByIds(userId, bookId);
        return "redirect:/loans/all";
    }

    // ==============================
    // Vrati knjigu (korisnik ili admin za svoje pozajmice)
    // ==============================
    @PostMapping("/return/{loanId}")
    public String returnLoan(@PathVariable Long loanId,
                             Authentication authentication) {
        try {
            loanService.returnLoan(loanId, authentication.getName());
        } catch (RuntimeException e) {

            System.out.println("Greška: " + e.getMessage());
        }

        return "redirect:/loans/active";
    }
}
