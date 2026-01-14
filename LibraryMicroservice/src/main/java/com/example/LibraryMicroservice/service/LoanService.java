package com.example.LibraryMicroservice.service;

import com.example.LibraryMicroservice.model.dto.LoanDto;
import com.example.LibraryMicroservice.model.entity.Loan;
import com.example.LibraryMicroservice.repository.LoanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    public List<LoanDto> getActiveLoansByUser(String username) {
        return loanRepository.findByUsername(username)
                .stream()
                .filter(loan -> !loan.isReturned())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setLoanId(loan.getId());
        dto.setBookId(loan.getBookId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReturned(loan.isReturned());
        dto.setBookTitle("Unknown");    // opcionalno
        dto.setBookAuthor("Unknown");   // opcionalno
        dto.setBookCategory("Unknown");// opcionalno
        return dto;
    }

    @Transactional
    public void borrowBook(String username, Long bookId) {
        // 1️⃣ Proveri da li korisnik već ima aktivnu pozajmicu za ovu knjigu
        loanRepository.findByUsernameAndBookIdAndReturnedFalse(username, bookId)
                .ifPresent(loan -> {
                    throw new RuntimeException("Korisnik je već pozajmio ovu knjigu.");
                });

        // 2️⃣ Kreiraj novu pozajmicu
        Loan loan = Loan.builder()
                .username(username)
                .bookId(bookId)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        loanRepository.save(loan);
    }

    @Transactional
    public void returnBook(String username, Long bookId) {
        Loan loan = loanRepository.findByUsernameAndBookIdAndReturnedFalse(username, bookId)
                .orElseThrow(() -> new RuntimeException("Aktivna pozajmica za ovu knjigu nije pronađena."));

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);
    }
}
