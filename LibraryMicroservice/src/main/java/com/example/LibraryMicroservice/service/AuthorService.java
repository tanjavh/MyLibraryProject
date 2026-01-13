package com.example.LibraryMicroservice.service;


import com.example.LibraryMicroservice.model.entity.Author;
import com.example.LibraryMicroservice.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    // ==============================
    // Dohvata sve autore
    // ==============================
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    // ==============================
    // Čuvanje ili kreiranje novog autora
    // ==============================
    public Author save(Author author) {
        if (author.getName() == null || author.getName().isBlank()) {
            throw new RuntimeException("Author name cannot be empty!");
        }
        return authorRepository.save(author);
    }

    // ==============================
    // Dohvata autora po ID-ju
    // ==============================
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    // ==============================
    // Brisanje autora
    // ==============================
    public void delete(Author author) {
        if (author != null) {
            authorRepository.delete(author);
        }
    }
}

