package com.example.onlineLibrary.service;

import com.example.onlineLibrary.model.dto.BookCreateDto;
import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final UserService userService;
    private final String libraryUrl = "http://localhost:8081/api/books";

    private final String libraryBaseUrl = "http://localhost:8081/api/books"; // LibraryMicroservice

    // ========================
    // Pozajmi knjigu
    // ========================
    @Transactional
    public void createLoanByIds(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen!"));

        // Provera da li korisnik već ima 3 aktivne pozajmice
        long activeLoans = loanRepository.findByUser(user).stream()
                .filter(l -> !l.isReturned())
                .count();
        if (activeLoans >= 3) {
            throw new RuntimeException("Korisnik već ima maksimalan broj aktivnih pozajmica.");
        }

        // Provera da li je već pozajmio ovu knjigu
        loanRepository.findByUserAndBookIdAndReturnedFalse(user, bookId)
                .ifPresent(l -> { throw new RuntimeException("Korisnik je već pozajmio ovu knjigu."); });

        // Provera dostupnosti knjige iz LibraryMicroservice
        BookCreateDto book = restTemplate.getForObject(libraryBaseUrl + "/" + bookId, BookCreateDto.class);
        if (book == null || !book.isAvailable()) {
            throw new RuntimeException("Knjiga nije dostupna.");
        }

        Loan loan = Loan.builder()
                .user(user)
                .bookId(bookId)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();
        loanRepository.save(loan);

        // Obeleži knjigu kao nedostupnu
        restTemplate.put(libraryBaseUrl + "/" + bookId + "/availability?available=false", null);
    }

    // ========================
    // Vrati knjigu
    // ========================
    @Transactional
    public void returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Pozajmica nije pronađena."));
        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);

        // Ažuriraj dostupnost knjige
        restTemplate.put(libraryBaseUrl + "/" + loan.getBookId() + "/availability?available=true", null);
    }

    // ========================
    // DTO konverzije
    // ========================
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
                dto.setBookAuthor(book.getNewAuthorName());
                dto.setBookCategory(book.getCategory().name());
            }
        } catch (Exception e) {
            dto.setBookTitle("Unknown");
            dto.setBookAuthor("Unknown");
            dto.setBookCategory("Unknown");
        }

        return dto;
    }

    // ========================
    // Thymeleaf metode
    // ========================
    @Transactional(readOnly = true)
    public List<LoanDto> getActiveLoansByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen: " + username));

        return loanRepository.findByUser(user).stream()
                .filter(l -> !l.isReturned())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanDto> getAllLoansDto() {
        return loanRepository.findAll()
                .stream()
                .map(loan -> {
                    LoanDto dto = new LoanDto();
                    dto.setLoanId(loan.getId());
                    dto.setBookId(loan.getBookId());
                    dto.setLoanDate(loan.getLoanDate());
                    dto.setReturnDate(loan.getReturnDate());
                    dto.setReturned(loan.isReturned());
                    dto.setUsername(loan.getUser().getUsername());

                    // knjiga se čita iz LibraryMicroservice
                    BookInfoResponse book =
                            restTemplate.getForObject(
                                    "http://localhost:8081/api/books/" + loan.getBookId(),
                                    BookInfoResponse.class
                            );

                    if (book != null) {
                        dto.setBookTitle(book.getTitle());
                        dto.setBookAuthor(book.getAuthorName());
                        dto.setBookCategory(book.getCategory().name());
                    }

                    return dto;
                })
                .toList();
    }


    @Transactional
    public void borrowBook(String username, Long bookId) {

        // 1️⃣ Pronađi korisnika
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        // 2️⃣ Provera: da li je korisnik već pozajmio ovu knjigu
        loanRepository.findByUserAndBookIdAndReturnedFalse(user, bookId)
                .ifPresent(l -> {
                    throw new RuntimeException("Knjiga je već pozajmljena");
                });

        // 3️⃣ Provera: max 3 aktivne pozajmice
        long activeLoans = loanRepository.findByUser(user).stream()
                .filter(l -> !l.isReturned())
                .count();

        if (activeLoans >= 3) {
            throw new RuntimeException("Dozvoljene su najviše 3 aktivne pozajmice");
        }

        // 4️⃣ Dohvati knjigu iz LibraryMicroservice
        BookInfoResponse book = restTemplate.getForObject(
                libraryUrl + "/" + bookId,
                BookInfoResponse.class
        );

        if (book == null || !book.isAvailable()) {
            throw new RuntimeException("Knjiga trenutno nije dostupna");
        }

        // 5️⃣ Kreiraj pozajmicu
        Loan loan = Loan.builder()
                .user(user)
                .bookId(bookId)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        loanRepository.save(loan);

        // 6️⃣ Obeleži knjigu kao nedostupnu u LibraryMicroservice
        restTemplate.put(
                libraryUrl + "/" + bookId + "/availability?available=false",
                null
        );
    }

}
