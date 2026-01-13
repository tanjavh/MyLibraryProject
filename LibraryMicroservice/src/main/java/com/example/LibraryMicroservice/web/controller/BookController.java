package com.example.LibraryMicroservice.web.controller;


import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.entity.Author;
import com.example.LibraryMicroservice.model.entity.Book;
import com.example.LibraryMicroservice.model.entity.Category;
import com.example.LibraryMicroservice.model.enums.CategoryName;
import com.example.LibraryMicroservice.service.AuthorService;
import com.example.LibraryMicroservice.service.BookService;
import com.example.LibraryMicroservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    // =======================
    // Lista svih knjiga
    // =======================
    @GetMapping
    public String allBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books";
    }

    // =======================
    // Kreiranje nove knjige - GET
    // =======================
    @GetMapping("/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new BookCreateDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", CategoryName.values());
        return "books-create";
    }

    // =======================
    // Kreiranje nove knjige - POST
    // =======================
    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") BookCreateDto dto) {
        bookService.createFromDto(dto);
        return "redirect:/books";
    }

    // =======================
    // Brisanje knjige
    // =======================
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}