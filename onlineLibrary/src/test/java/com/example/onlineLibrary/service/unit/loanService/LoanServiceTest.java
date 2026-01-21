package com.example.onlineLibrary.service.unit.loanService;

import com.example.onlineLibrary.model.dto.BookInfoResponse;
import com.example.onlineLibrary.model.dto.LoanDto;
import com.example.onlineLibrary.model.entity.Loan;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.CategoryName;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LoanService loanService;

    private User testUser;
    private Loan loanExistingBook;
    private Loan loanDeletedBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");
        testUser.setBlocked(false);

        // Pozajmica sa postojećom knjigom
        loanExistingBook = Loan.builder()
                .id(1L)
                .user(testUser)
                .bookId(100L)
                .bookTitle("Existing Book")
                .loanDate(LocalDate.now().minusDays(20))
                .returned(false)
                .build();

        // Pozajmica sa obrisanom knjigom
        loanDeletedBook = Loan.builder()
                .id(2L)
                .user(testUser)
                .bookId(101L)
                .bookTitle("Deleted Book")
                .loanDate(LocalDate.now().minusDays(20))
                .returned(false)
                .build();
    }

    @Test
    void testActiveLoanWithExistingBook() {
        when(loanRepository.findActiveLoansByUsername("user1"))
                .thenReturn(List.of(loanExistingBook));

        // Mock REST poziva za postojeću knjigu
        BookInfoResponse bookResponse = BookInfoResponse.builder()
                .id(100L)
                .title("Existing Book")
                .authorName("Autor Test")
                .category(CategoryName.CLASSIC)
                .available(true)
                .year(2020)
                .build();

        when(restTemplate.getForObject(
                "http://localhost:8081/api/books/100",
                BookInfoResponse.class
        )).thenReturn(bookResponse);

        List<LoanDto> loans = loanService.getActiveLoansByUser("user1");

        assertThat(loans).hasSize(1);
        LoanDto dto = loans.get(0);

        assertThat(dto.getBookTitle()).isEqualTo("Existing Book");
        assertThat(dto.getBookAuthor()).isEqualTo("Autor Test");
        assertThat(dto.getBookCategory()).isEqualTo(CategoryName.CLASSIC);
        assertThat(dto.isOverdue()).isTrue(); // 20 dana > 15
    }

    @Test
    void testActiveLoanWithDeletedBook() {
        when(loanRepository.findActiveLoansByUsername("user1"))
                .thenReturn(List.of(loanDeletedBook));

        // Mock REST poziva → knjiga je obrisana (null)
        when(restTemplate.getForObject(
                "http://localhost:8081/api/books/101",
                BookInfoResponse.class
        )).thenReturn(null);

        List<LoanDto> loans = loanService.getActiveLoansByUser("user1");

        assertThat(loans).hasSize(1);
        LoanDto dto = loans.get(0);

        assertThat(dto.getBookTitle()).isEqualTo("Deleted Book (obrisana)");
        assertThat(dto.getBookAuthor()).isEqualTo("N/A");
        assertThat(dto.getBookCategory()).isNull();
        assertThat(dto.isOverdue()).isTrue();
    }
    @Test
    void newLoan_shouldBeNotReturnedByDefault() {
        Loan loan = Loan.builder()
                .bookId(1L)
                .bookTitle("Test book")
                .loanDate(LocalDate.now())
                .build();

        assertThat(loan.isReturned()).isFalse();
    }


}
