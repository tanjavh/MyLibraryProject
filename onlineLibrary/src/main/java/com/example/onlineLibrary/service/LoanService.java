package com.example.onlineLibrary.service;

import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.CategoryName;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private final String libraryBaseUrl = "http://localhost:8081/api/books";

    // ==============================
    // Vrati sve pozajmice za korisnika
    // ==============================
    public LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setLoanId(loan.getId());
        dto.setBookId(loan.getBookId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReturned(loan.isReturned());
        dto.setUsername(loan.getUser().getUsername());

        LocalDate dueDate = loan.getLoanDate().plusDays(15);
        dto.setDueDate(dueDate);
        dto.setOverdue(!loan.isReturned() && LocalDate.now().isAfter(dueDate));

        try {
            BookInfoResponse book = restTemplate.getForObject(
                    libraryBaseUrl + "/" + loan.getBookId(),
                    BookInfoResponse.class
            );

            if (book != null) {
                dto.setBookTitle(book.getTitle());
                dto.setBookAuthor(book.getAuthorName());
                dto.setBookCategory(book.getCategory());
            } else {
                dto.setBookTitle(loan.getBookTitle() + " (Obrisana)");
                dto.setBookAuthor("-");
                dto.setBookCategory(CategoryName.UNKNOWN);
            }

        } catch (Exception e) {
            dto.setBookTitle(loan.getBookTitle() + " (Obrisana)");
            dto.setBookAuthor("-");
            dto.setBookCategory(CategoryName.UNKNOWN);
        }


        return dto;
    }
    @Transactional(readOnly = true)
    public List<LoanDto> getActiveLoansByUser(String username) {
        return loanRepository.findByUserUsername(username).stream()
                .map(loan -> convertToDto(loan))
                .toList();
    }

    // ==============================
    // Sve pozajmice (admin)
    // ==============================
    @Transactional(readOnly = true)
    public List<LoanDto> getAllLoansDto() {
        return loanRepository.findAll().stream()
                .map(this::convertToDto)
                .filter(dto -> dto != null) // uklanjamo obrisane knjige
                .toList();
    }

    // ==============================
    // Kreiranje pozajmice
    // ==============================
    @Transactional
    public void createLoanByIds(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBookId(bookId);
        loan.setLoanDate(LocalDate.now());
        loan.setReturned(false);

        loanRepository.save(loan);

        // Označi knjigu kao nedostupnu
        try {
            restTemplate.put(libraryBaseUrl + "/" + bookId + "/availability?available=false", null);
        } catch (Exception e) {
            // REST nije dostupan, samo logujemo
            System.out.println("Ne mogu da update-ujem dostupnost knjige: " + e.getMessage());
        }
    }

    // ==============================
    // Vrati knjigu - samo vlasnik može
    // ==============================
    @Transactional
    public void returnLoan(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Pozajmica nije pronađena."));

        // Provera vlasništva
        if (!loan.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Ne možeš da vratiš tuđu knjigu!");
        }

        // Obeležavanje pozajmice kao vraćene
        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);

        // Update dostupnosti knjige u LibraryMicroservice
        try {
            restTemplate.put(
                    libraryBaseUrl + "/" + loan.getBookId() + "/availability?available=true",
                    null
            );
        } catch (Exception e) {
            System.out.println("Ne mogu da update-ujem dostupnost knjige: " + e.getMessage());
        }
    }

    @Transactional
    public void borrowBook(String username, Long bookId) {
        // 1️⃣ Nađi korisnika
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));
        if (user.isBlocked()) {
            throw new RuntimeException("Blokirani korisnici ne mogu pozajmiti nove knjige!");
        }

        // 2️⃣ Proveri da li je knjiga dostupna (pozovi LibraryMicroservice)
        BookInfoResponse book;
        try {
            book = restTemplate.getForObject(libraryBaseUrl + "/" + bookId, BookInfoResponse.class);
            if (book == null || !book.isAvailable()) {
                throw new RuntimeException("Knjiga nije dostupna za pozajmicu");
            }
        } catch (Exception e) {
            throw new RuntimeException("Greška pri povezivanju sa LibraryMicroservice: " + e.getMessage());
        }

        // 3️⃣ Kreiraj novu pozajmicu i sačuvaj naslov knjige
        Loan loan = Loan.builder()
                .bookId(bookId)
                .bookTitle(book.getTitle()) // <-- ovde čuvamo naslov
                .user(user)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();
        loanRepository.save(loan);

        // 4️⃣ Update dostupnosti knjige u LibraryMicroservice
        try {
            restTemplate.put(
                    libraryBaseUrl + "/" + bookId + "/availability?available=false",
                    null
            );
        } catch (Exception e) {
            System.out.println("Ne mogu da update-ujem dostupnost knjige: " + e.getMessage());
        }
    }

    public void returnBookByBookId(Long bookId, String username) {
    }

    // Proverava da li neka knjiga ima aktivnu pozajmicu
    public boolean existsByBookIdAndReturnedFalse(Long bookId) {
        return loanRepository.existsByBookIdAndReturnedFalse(bookId);
    }
}
