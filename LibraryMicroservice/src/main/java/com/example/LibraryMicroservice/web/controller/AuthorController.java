package com.example.LibraryMicroservice.web.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import com.example.LibraryMicroservice.model.entity.Author;
import com.example.LibraryMicroservice.service.AuthorService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public String listAuthors(Model model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "all-authors";
    }

    @GetMapping("/create")
    public String createAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "create-author";
    }

    @PostMapping("/create")
    public String createAuthor(@ModelAttribute Author author) {
        authorService.save(author);
        return "redirect:/authors";
    }


    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.findById(id).ifPresent(authorService::delete);
        return "redirect:/authors";
    }
}
