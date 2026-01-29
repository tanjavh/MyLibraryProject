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

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author save(Author author) {
        if (author.getName() == null || author.getName().isBlank()) {
            throw new RuntimeException("Ime autora ne može biti prazan string!");
        }
        return authorRepository.save(author);
    }

    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    public void delete(Author author) {
        if (author != null) {
            authorRepository.delete(author);
        }
    }

    public Optional<Author> findByName(String newAuthorName) {
        return authorRepository.findByName(newAuthorName);
    }
}

