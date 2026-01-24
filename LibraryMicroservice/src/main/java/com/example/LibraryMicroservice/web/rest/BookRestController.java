package com.example.LibraryMicroservice.web.rest;

import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.dto.BookInfoResponse;
import com.example.LibraryMicroservice.service.BookService;
import lombok.RequiredArgsConstructor;
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
    private final String loanServiceUrl = "http://localhost:8082/api/loans"; // OnlineLibrary

    // Pozajmi knjigu
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

    // Vrati knjigu
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

    @PostMapping
    public ResponseEntity<Void> createBook(@RequestBody BookCreateDto dto) {
        bookService.createFromDto(dto);
        return ResponseEntity.status(201).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookInfoResponse> getBookById(@PathVariable Long id) {
        BookInfoResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    @PutMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {

        bookService.updateAvailability(id, available);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public List<BookInfoResponse> getAllBooks(
            @RequestParam(required = false) String sort) {

        return bookService.findAllSorted(sort).stream()
                .map(bookService::toBookInfo)
                .toList();
    }

}