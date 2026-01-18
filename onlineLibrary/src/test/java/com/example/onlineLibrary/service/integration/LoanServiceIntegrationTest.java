package com.example.onlineLibrary.service.integration;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class LoanServiceIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .username("integrationUser")
                .email("integration@example.com")
                .password("pass")
                .active(true)
                .blocked(false)
                .build();
        userRepository.save(testUser);

        // Pozajmica između 15 i 30 dana
        Loan loan = Loan.builder()
                .user(testUser)
                .bookId(1L)
                .bookTitle("Integration Book")
                .loanDate(LocalDate.now().minusDays(20))
                .returned(false)
                .build();
        loanRepository.save(loan);
    }

    @Test
    @Transactional
    void testOverdueFlagIntegration() {
        List<LoanDto> loans = loanService.getActiveLoansByUser(testUser.getUsername());

        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).isOverdue()).isTrue();
        assertThat(loans.get(0).getBookTitle()).isEqualTo("Integration Book");
    }
}
