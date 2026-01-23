package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.model.dto.BookCreateDto;
import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.CategoryName;

import com.example.onlineLibrary.service.LoanService;
import com.example.onlineLibrary.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final RestTemplate restTemplate;
    private final LoanService loanService;
    private final UserService userService;
    private final String libraryUrl = "http://localhost:8081/api/books";

    // ==============================
    // Prikaz svih knjiga
    // ==============================
    @GetMapping
    public String allBooks(Model model, Authentication authentication) {
        try {
            BookInfoResponse[] books =
                    restTemplate.getForObject(libraryUrl, BookInfoResponse[].class);

            List<BookInfoResponse> bookList =
                    books != null ? List.of(books) : new ArrayList<>();

            bookList.forEach(book -> {
                boolean hasActiveLoans =
                        loanService.existsByBookIdAndReturnedFalse(book.getId());
                book.setHasActiveLoans(hasActiveLoans);
            });

            model.addAttribute("books", bookList);

            if (authentication != null) {
                model.addAttribute("currentUsername", authentication.getName());
            }

            return "books";

        } catch (ResourceAccessException ex) {
            throw ex; // IDE U @ControllerAdvice
        }
    }

    // ==============================
    // Kreiranje nove knjige (ADMIN)
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("book", new BookCreateDto());
        model.addAttribute("categories", CategoryName.values());
        return "books-create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") BookCreateDto dto) {
        restTemplate.postForObject(libraryUrl, dto, Void.class);
        return "redirect:/books";
    }

    // ==============================
    // Brisanje knjige (ADMIN) samo ako nije pozajmljena
    // ==============================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        boolean hasActiveLoans = loanService.existsByBookIdAndReturnedFalse(id);
        if (hasActiveLoans) {
            // možeš dodati flash atribut za poruku u view
            return "redirect:/books?error=activeLoan";
        }

        restTemplate.delete(libraryUrl + "/" + id);
        return "redirect:/books";
    }

    // ==============================
    // Pozajmi knjigu
    // ==============================
    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null || currentUser.isBlocked()) {
            return "redirect:/books?error=blocked";
        }

        loanService.borrowBook(currentUser.getUsername(), bookId);
        return "redirect:/books";
    }

    // ==============================
    // Vrati knjigu
    // ==============================
    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable Long bookId, Principal principal) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        loanService.returnBookByBookId(bookId, currentUser.getUsername());
        return "redirect:/books";
    }
}
