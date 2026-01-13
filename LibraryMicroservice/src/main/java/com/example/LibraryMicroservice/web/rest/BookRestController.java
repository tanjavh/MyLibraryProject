package com.example.LibraryMicroservice.web.rest;


import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.dto.BookInfoResponse;
import com.example.LibraryMicroservice.model.entity.Book;
import com.example.LibraryMicroservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;
    private final RestTemplate restTemplate;
    private final String loanServiceUrl = "http://localhost:8082/api/loans";

    @GetMapping
    public List<BookInfoResponse> getAllBooks() {
        return bookService.findAll().stream()
                .map(bookService::toBookInfo)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookInfoResponse> getBookById(@PathVariable Long id) {
        return bookService.findById(id)
                .map(bookService::toBookInfo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookInfoResponse> createBook(@RequestBody BookCreateDto dto) {
        Book saved = bookService.createFromDto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.toBookInfo(saved));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id,
                                                   @RequestParam boolean available) {
        bookService.updateAvailability(id, available);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{bookId}/borrow/{userId}")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId,
                                             @PathVariable Long userId) {
        try {
            restTemplate.postForObject(
                    loanServiceUrl + "/borrow?userId=" + userId + "&bookId=" + bookId,
                    null,
                    String.class
            );
            return ResponseEntity.ok("Knjiga je pozajmljena!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }

    }
    @PostMapping("/{bookId}/return/{loanId}")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId,
                                             @PathVariable Long loanId) {
        try {
            restTemplate.postForObject(
                    loanServiceUrl + "/return?loanId=" + loanId,
                    null,
                    String.class
            );
            return ResponseEntity.ok("Knjiga je vraćena!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }


}
