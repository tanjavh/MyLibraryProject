package com.example.onlineLibrary.service.integration;

import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
class LoanServiceIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private org.springframework.web.client.RestTemplate restTemplate;

    private User testUser;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .username("integrationUser")
                .email("integration@example.com")
                .password("password123")
                .active(true)
                .blocked(false)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void testLoanWithExistingBook() {
        Loan loan = Loan.builder()
                .user(testUser)
                .bookId(1L)
                .bookTitle("Existing Book")
                .loanDate(LocalDate.now().minusDays(20))
                .returned(false)
                .build();
        loanRepository.save(loan);

        BookInfoResponse bookResponse = BookInfoResponse.builder()
                .id(1L)
                .title("Existing Book")
                .authorName("Autor Test")
                .category(null)
                .year(2020)
                .available(false)
                .build();

        when(restTemplate.getForObject("http://localhost:8081/api/books/1", BookInfoResponse.class))
                .thenReturn(bookResponse);

        List<LoanDto> loans = loanService.getActiveLoansByUser(testUser.getUsername());

        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).getBookTitle()).isEqualTo("Existing Book");
        assertThat(loans.get(0).getBookAuthor()).isEqualTo("Autor Test");
        assertThat(loans.get(0).isOverdue()).isTrue();
    }

    @Test
    void testLoanWithDeletedBook() {
        Loan loan = Loan.builder()
                .user(testUser)
                .bookId(2L)
                .bookTitle("Deleted Book")
                .loanDate(LocalDate.now().minusDays(20))
                .returned(false)
                .build();
        loanRepository.save(loan);

        when(restTemplate.getForObject("http://localhost:8081/api/books/2", BookInfoResponse.class))
                .thenReturn(null);

        List<LoanDto> loans = loanService.getActiveLoansByUser(testUser.getUsername());

        assertThat(loans).hasSize(1);
        assertThat(loans.get(0).getBookTitle()).isEqualTo("Deleted Book (obrisana)");
        assertThat(loans.get(0).getBookAuthor()).isEqualTo("N/A");
        assertThat(loans.get(0).isOverdue()).isTrue();
    }
}