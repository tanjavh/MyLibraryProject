package com.example.onlineLibrary.service.integration;

import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
@SpringBootTest
public class LoanTestsMoreThan15AndMoreThan30Days {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanService loanService;
    @Test
    void whenLoanIsOlderThan15Days_shouldShowOverdue15Message() {
        User user = userRepository.save(
                User.builder()
                        .username("user15")
                        .email("u15@test.com")
                        .password("password123")
                        .active(true)
                        .blocked(false)
                        .build()
        );

        loanRepository.save(
                Loan.builder()
                        .user(user)
                        .bookId(1L)
                        .bookTitle("Book")
                        .loanDate(LocalDate.now().minusDays(16))
                        .returned(false)
                        .build()
        );

        List<LoanDto> loans =
                loanService.getActiveLoansByUser("user15");

        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).isOverdue()).isTrue();
        assertThat(loans.get(0).getOverdueMessage())
                .isEqualTo("Rok za vraćanje je prekoračen (15+ dana)");
    }
    @Test
    void whenLoanIsOlderThan30Days_userShouldBeBlockedAndMessageShown() {

        User user = userRepository.save(
                User.builder()
                        .username("user30")
                        .email("u30@test.com")
                        .password("password123")
                        .active(true)
                        .blocked(false)
                        .build()
        );

        loanRepository.save(
                Loan.builder()
                        .user(user)
                        .bookId(2L)
                        .bookTitle("Old Book")
                        .loanDate(LocalDate.now().minusDays(31))
                        .returned(false)
                        .build()
        );

        // ✅ KLJUČNO
        loanService.blockUsersWithLoansOlderThan30Days();

        User updated =
                userRepository.findByUsername("user30").orElseThrow();

        assertThat(updated.isBlocked()).isTrue();

        // poruka i dalje dolazi iz DTO-a
        List<LoanDto> loans =
                loanService.getActiveLoansByUser("user30");

        assertThat(loans.get(0).getOverdueMessage())
                .contains("Vaš nalog je blokiran");
    }
}
