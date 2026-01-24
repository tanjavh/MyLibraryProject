package com.example.onlineLibrary.service.unit.loanService;

import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoanServiceUnitTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("testuser");
    }

    @Test
    void testOverdueFlagBetween15And30Days() {
        Loan loan = Loan.builder()
                .loanDate(LocalDate.now().minusDays(20)) // između 15 i 30
                .returned(false)
                .user(user)
                .bookId(1L)
                .bookTitle("Book 1")
                .build();

        when(loanRepository.findActiveLoansByUsername("testuser"))
                .thenReturn(List.of(loan));

        List<LoanDto> loans = loanService.getActiveLoansByUser("testuser");

        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).isOverdue()).isTrue();
    }

    @Test
    void testNotOverdueIfLessThan15Days() {
        Loan loan = Loan.builder()
                .loanDate(LocalDate.now().minusDays(10))
                .returned(false)
                .user(user)
                .bookId(2L)
                .bookTitle("Book 2")
                .build();

        when(loanRepository.findActiveLoansByUsername("testuser"))
                .thenReturn(List.of(loan));

        List<LoanDto> loans = loanService.getActiveLoansByUser("testuser");

        assertThat(loans.get(0).isOverdue()).isFalse();
    }

    @Test
    void testOverdueIfMoreThan30Days() {
        Loan loan = Loan.builder()
                .loanDate(LocalDate.now().minusDays(31))
                .returned(false)
                .user(user)
                .bookId(3L)
                .bookTitle("Book 3")
                .build();

        when(loanRepository.findActiveLoansByUsername("testuser"))
                .thenReturn(List.of(loan));

        List<LoanDto> loans = loanService.getActiveLoansByUser("testuser");

        assertThat(loans.get(0).isOverdue()).isTrue();
    }

}