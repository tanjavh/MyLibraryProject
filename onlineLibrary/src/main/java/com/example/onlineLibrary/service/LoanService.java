package com.example.onlineLibrary.service;

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

    private final String libraryBaseUrl = "http://localhost:8081/api/books";

    // ==============================
    // Konverzija Loan -> LoanDto
    // ==============================
    public LoanDto convertToDto(Loan loan) {

        LoanDto dto = new LoanDto();

        dto.setLoanId(loan.getId());
        dto.setBookId(loan.getBookId());
        dto.setBookTitle(loan.getBookTitle());
        dto.setUsername(loan.getUser().getUsername());
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());

        // ⏱️ Izračunavanje dana
        long days =
                java.time.temporal.ChronoUnit.DAYS.between(
                        loan.getLoanDate(),
                        LocalDate.now()
                );

        // ⚠️ Prekoračenje 15+ dana
        if (!loan.isReturned() && days > 15) {
            dto.setOverdue(true);
            dto.setOverdueMessage("Rok za vraćanje je prekoračen (15+ dana)");
        }

        // ⛔ Prekoračenje 30+ dana (samo PORUKA)
        if (!loan.isReturned() && days > 30) {
            dto.setOverdue(true);
            dto.setOverdueMessage(
                    "⚠️ Vaš nalog je blokiran zbog nevraćenih knjiga starijih od 30 dana."
            );
        }

        // 📚 Dohvati podatke o knjizi iz LibraryMicroservice
        try {
            BookInfoResponse book = restTemplate.getForObject(
                    libraryBaseUrl + "/" + loan.getBookId(),
                    BookInfoResponse.class
            );

            if (book != null) {
                dto.setBookAuthor(book.getAuthorName());
                dto.setBookCategory(book.getCategory());
            } else {
                dto.setBookTitle(dto.getBookTitle() + " (obrisana)");
                dto.setBookAuthor("N/A");
                dto.setBookCategory(null);
            }

        } catch (Exception e) {
            dto.setBookTitle(dto.getBookTitle() + " (obrisana)");
            dto.setBookAuthor("N/A");
            dto.setBookCategory(null);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<LoanDto> getActiveLoansByUser(String username) {
        List<Loan> loans = loanRepository.findActiveLoansByUsername(username);
        return loans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Loan returnLoan(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Pozajmica nije pronađena."));

        if (!loan.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Ne možeš da vratiš tuđu knjigu!");
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);

        try {
            restTemplate.put(
                    libraryBaseUrl + "/" + loan.getBookId() + "/availability?available=true",
                    null
            );
        } catch (Exception e) {
            System.out.println("Ne mogu da update-ujem dostupnost knjige: " + e.getMessage());
        }
        return loan;
    }

    @Transactional
    public void createLoanByIds(Long userId, Long bookId) {
        // 1️⃣ Nađi korisnika
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        // 2️⃣ Pozovi LibraryMicroservice da dobiješ detalje knjige
        BookInfoResponse book;
        try {
            book = restTemplate.getForObject(libraryBaseUrl + "/" + bookId, BookInfoResponse.class);
            if (book == null || !book.isAvailable()) {
                throw new RuntimeException("Knjiga nije dostupna za pozajmicu");
            }
        } catch (Exception e) {
            throw new RuntimeException("Greška pri povezivanju sa LibraryMicroservice: " + e.getMessage());
        }

        // 3️⃣ Kreiraj pozajmicu i sačuvaj naslov knjige
        Loan loan = Loan.builder()
                .user(user)
                .bookId(bookId)
                .bookTitle(book.getTitle())
                .returned(false)
                .loanDate(LocalDate.now())
                .build();
        loanRepository.save(loan);

        // 4️⃣ Označi knjigu kao nedostupnu u LibraryMicroservice
        try {
            restTemplate.put(libraryBaseUrl + "/" + bookId + "/availability?available=false", null);
        } catch (Exception e) {
            System.out.println("Ne mogu da update-ujem dostupnost knjige: " + e.getMessage());
        }
    }


    @Transactional(readOnly = true)
    public List<LoanDto> getAllLoansDto() {
        List<Loan> loans = loanRepository.findAll();

        return loans.stream()
                .map(loan -> {
                    LoanDto dto = convertToDto(loan); // postojeći convertToDto
                    try {
                        // Popuni dodatna polja preko LibraryMicroservice
                        BookInfoResponse book = restTemplate.getForObject(
                                libraryBaseUrl + "/" + loan.getBookId(),
                                BookInfoResponse.class
                        );
                        if (book != null) {
                            dto.setBookAuthor(book.getAuthorName());
                            dto.setBookCategory(book.getCategory());
                        }
                    } catch (Exception e) {
                        System.out.println("Ne mogu da dobijem detalje knjige: " + e.getMessage());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean existsByBookIdAndReturnedFalse(Long bookId) {
        return loanRepository.existsByBookIdAndReturnedFalse(bookId);
    }

    @Transactional
    public void borrowBook(String username, Long bookId) {

        // 1️⃣ Pronađi korisnika
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        // 2️⃣ Proveri da li je korisnik blokiran
        if (user.isBlocked()) {
            throw new RuntimeException("Blokirani korisnici ne mogu pozajmiti nove knjige!");
        }

        // ✅ 2.5️⃣ PROVERA MAKSIMUMA (NOVO, ALI NA PRAVOM MESTU)
        int activeLoans =
                loanRepository.countByUser_UsernameAndReturnedFalse(username);

        if (activeLoans >= 3) {
            throw new IllegalStateException(
                    "Možete imati najviše 3 pozajmljene knjige istovremeno."
            );
        }

        // 3️⃣ Proveri dostupnost knjige u LibraryMicroservice
        BookInfoResponse book;
        try {
            book = restTemplate.getForObject(
                    libraryBaseUrl + "/" + bookId,
                    BookInfoResponse.class
            );

            if (book == null || !book.isAvailable()) {
                throw new RuntimeException("Knjiga nije dostupna za pozajmicu");
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Greška pri povezivanju sa LibraryMicroservice: " + e.getMessage()
            );
        }

        // 4️⃣ Kreiraj novu pozajmicu
        Loan loan = Loan.builder()
                .bookId(bookId)
                .bookTitle(book.getTitle())
                .user(user)
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        loanRepository.save(loan);

        // 5️⃣ Update dostupnosti knjige u LibraryMicroservice
        try {
            restTemplate.put(
                    libraryBaseUrl + "/" + bookId + "/availability?available=false",
                    null
            );
        } catch (Exception e) {
            System.out.println(
                    "Ne mogu da update-ujem dostupnost knjige: " + e.getMessage()
            );
        }
    }


    @Transactional
    public void returnBookByBookId(Long bookId, String username) {
        // 1️⃣ Pronađi aktivnu pozajmicu za datu knjigu i korisnika
        Loan loan = loanRepository
                .findByBookIdAndUserUsernameAndReturnedFalse(bookId, username)
                .orElseThrow(() ->
                        new IllegalStateException("Ne postoji aktivna pozajmica za ovu knjigu"));

        // 2️⃣ Obeleži pozajmicu kao vraćenu
        loan.setReturned(true);
        loan.setReturnDate(LocalDate.now());
        loanRepository.save(loan);

        // 3️⃣ Update dostupnosti knjige u LibraryMicroservice
        try {
            restTemplate.put(
                    libraryBaseUrl + "/" + bookId + "/availability?available=true",
                    null
            );
        } catch (Exception e) {
            System.out.println("Ne mogu da update-ujem dostupnost knjige: " + e.getMessage());
        }
    }


    public boolean isBorrowedByUser(Long bookId, String currentUsername) {
        return loanRepository
                .existsByBookIdAndUserUsernameAndReturnedFalse(bookId, currentUsername);
    }

    public int countActiveLoansByUsername(String username) {
        return loanRepository
                .countByUser_UsernameAndReturnedFalse(username);
    }
    @Transactional
    public void blockUsersWithLoansOlderThan30Days() {

        LocalDate limit = LocalDate.now().minusDays(30);

        List<Loan> overdueLoans =
                loanRepository.findAll().stream()
                        .filter(l -> !l.isReturned())
                        .filter(l -> l.getLoanDate().isBefore(limit))
                        .toList();

        for (Loan loan : overdueLoans) {
            User user = loan.getUser();
            if (!user.isBlocked()) {
                user.setBlocked(true);
                userRepository.save(user);
            }
        }
    }

}