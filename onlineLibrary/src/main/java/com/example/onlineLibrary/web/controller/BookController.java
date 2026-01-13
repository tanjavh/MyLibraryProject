package com.example.onlineLibrary.web.controller;

import com.example.onlineLibrary.model.dto.BookCreateDto;
import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final RestTemplate restTemplate;
    private final String libraryUrl = "http://localhost:8081/api/books";

    @GetMapping
    public String allBooks(Model model) {
        BookInfoResponse[] books = restTemplate.getForObject(libraryUrl, BookInfoResponse[].class);
        model.addAttribute("books", List.of(books));
        return "books";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("book", new BookCreateDto());
        model.addAttribute("categories", CategoryName.values());
        return "books-create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create") // samo "/create"
    public String createBook(@ModelAttribute("book") BookCreateDto dto) {
        restTemplate.postForObject(libraryUrl, dto, Void.class);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        restTemplate.delete(libraryUrl + "/" + id);
        return "redirect:/books";
    }
}
