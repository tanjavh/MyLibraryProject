package com.example.LibraryMicroservice.web.controller;

import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.enums.CategoryName;
import com.example.LibraryMicroservice.service.AuthorService;
import com.example.LibraryMicroservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;


    @GetMapping
    public String allBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books";
    }

    @GetMapping("/create")
    public String createBookForm(Model model) {
        model.addAttribute("book", new BookCreateDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", CategoryName.values());
        return "create-book";
    }

    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") BookCreateDto dto, Model model) {
        try {
            bookService.createFromDto(dto);
            return "redirect:/books";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("book", dto);
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("categories", CategoryName.values());
            return "create-book";
        }
    }


    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}