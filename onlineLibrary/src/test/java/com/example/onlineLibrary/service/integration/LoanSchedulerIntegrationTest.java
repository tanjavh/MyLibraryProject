
package com.example.onlineLibrary.service.integration;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.LoanSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test") // koristi application-test.properties (H2)
@SpringBootTest
class LoanSchedulerIntegrationTest {

    @Autowired
    private LoanSchedulerService loanSchedulerService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Očisti bazu pre svakog testa
        loanRepository.deleteAll();
        userRepository.deleteAll();

        // Kreiraj korisnika sa jedinstvenim emailom
        long timestamp = System.currentTimeMillis();
        testUser = User.builder()
                .username("testuser" + timestamp)
                .email("test+" + timestamp + "@example.com")
                .password("password")
                .active(true)
                .blocked(false)
                .build();
        userRepository.save(testUser);

        // Pozajmica starija od 31 dan (treba da blokira korisnika)
        Loan overdueLoan = Loan.builder()
                .bookId(1L)
                .bookTitle("Old Book")
                .loanDate(LocalDate.now().minusDays(31))
                .returnDate(null)
                .returned(false)
                .user(testUser)
                .build();
        loanRepository.save(overdueLoan);

        // Pozajmica novija od 10 dana (ne treba da blokira korisnika)
        Loan recentLoan = Loan.builder()
                .bookId(2L)
                .bookTitle("Recent Book")
                .loanDate(LocalDate.now().minusDays(10))
                .returnDate(null)
                .returned(false)
                .user(testUser)
                .build();
        loanRepository.save(recentLoan);
    }

    @Test
    @Transactional
    void testBlockOverdueUsers() {
        // Pre blokade korisnik ne sme biti blokiran
        User before = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(before.isBlocked()).isFalse();

        // Pozovi scheduler
        loanSchedulerService.blockOverdueUsers();

        // Nakon scheduler-a korisnik treba da bude blokiran zbog stare pozajmice
        User after = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(after.isBlocked()).isTrue();
    }
}
