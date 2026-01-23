package com.example.onlineLibrary.service.integration;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.LoanSchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional // sve promene se rollback-uju posle testa
class LoanSchedulerServiceTest {

    @Autowired
    private LoanSchedulerService loanSchedulerService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testBlockOverdueUsers() {
        // 1️⃣ Kreiramo testnog korisnika sa svim potrebnim poljima
        User user = new User();
        user.setUsername("testuser" + System.currentTimeMillis());
        user.setEmail("test+" + System.currentTimeMillis() + "@example.com");
        user.setPassword("password123");
        user.setActive(true);
        user.setBlocked(false);
        userRepository.save(user);

        // 2️⃣ Kreiramo loan koji je pre 31 dan (preko 30 dana)
        Loan loan = Loan.builder()
                .user(user)
                .bookId(1L) // dummy bookId
                .bookTitle("Test Book") // dummy title
                .loanDate(LocalDate.now().minusDays(31))
                .returned(false)
                .build();
        loanRepository.save(loan);

        // 3️⃣ Poziv metode koja blokira korisnike
        loanSchedulerService.blockOverdueUsers();

        // 4️⃣ Provera da li je korisnik blokiran
        User blockedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(blockedUser.isBlocked(), "Korisnik treba biti blokiran");
    }
}
