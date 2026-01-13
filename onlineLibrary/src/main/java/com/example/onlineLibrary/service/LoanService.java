package com.example.onlineLibrary.service;

import com.example.onlineLibrary.model.dto.BookCreateDto;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final RestTemplate restTemplate;

    private final String libraryBaseUrl = "http://localhost:8081/api/books";

    // =======================
    // Kreiranje nove pozajmice
    // =======================
    @Transactional
    public Loan createLoan(User user, Long bookId) {
        BookCreateDto book = restTemplate.getForObject(libraryBaseUrl + "/" + bookId, BookCreateDto.class);
        if (book == null) throw new RuntimeException("Knjiga ne postoji!");
        if (!book.isAvailable()) throw new RuntimeException("Knjiga nije dostupna!");

        Loan loan = Loan.builder()
                .user(user)
                .bookId(bookId)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        // Obeležavanje knjige kao nedostupne
        book.setAvailable(false);
        restTemplate.put(libraryBaseUrl + "/" + bookId, book);

        return loanRepository.save(loan);
    }

    // =======================
    // Vraćanje pozajmljene knjige
    // =======================
    @Transactional
    public void returnBook(Loan loan) {
        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);

        BookCreateDto book = restTemplate.getForObject(libraryBaseUrl + "/" + loan.getBookId(), BookCreateDto.class);
        if (book != null) {
            book.setAvailable(true);
            restTemplate.put(libraryBaseUrl + "/" + loan.getBookId(), book);
        }
    }

    // =======================
    // DTO konverzija
    // =======================
    public LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setLoanId(loan.getId());
        dto.setBookId(loan.getBookId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReturned(loan.isReturned());

        try {
            BookCreateDto book = restTemplate.getForObject(libraryBaseUrl + "/" + loan.getBookId(), BookCreateDto.class);
            if (book != null) {
                dto.setBookTitle(book.getTitle());
                dto.setBookAuthor(book.getAuthorName());
                dto.setBookCategory(book.getCategory().name());
            }
        } catch (Exception e) {
            dto.setBookTitle("Unknown");
            dto.setBookAuthor("Unknown");
            dto.setBookCategory("Unknown");
        }

        return dto;
    }

    // =======================
    // Sve pozajmice korisnika
    // =======================
    public List<LoanDto> getLoansByUser(User user) {
        return loanRepository.findByUser(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // =======================
    // Samo aktivne pozajmice korisnika
    // =======================
    public List<LoanDto> getActiveLoansByUser(User user) {
        return loanRepository.findByUser(user)
                .stream()
                .filter(loan -> !loan.isReturned())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // =======================
    // Ostale metode
    // =======================
    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
