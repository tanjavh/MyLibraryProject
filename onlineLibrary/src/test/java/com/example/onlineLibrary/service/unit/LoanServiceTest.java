package com.example.onlineLibrary.service.unit;

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
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user1");
        testUser.setBlocked(false);

        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setUser(testUser);
        testLoan.setBookId(100L);
        testLoan.setLoanDate(LocalDate.now().minusDays(10)); // 10 dana unazad
        testLoan.setReturned(false);
    }

    @Test
    void testGetActiveLoansByUser_dueDateOverdue() {
        when(loanRepository.findByUserUsername("user1"))
                .thenReturn(List.of(testLoan));

        // mock REST poziva
        BookInfoResponse bookResponse = BookInfoResponse.builder()
                .id(100L)
                .title("Test Book")
                .authorName("Test Author")
                .category(CategoryName.FICTION)
                .available(true)
                .year(2020)
                .build();

        when(restTemplate.getForObject("http://localhost:8081/api/books/100", BookInfoResponse.class))
                .thenReturn(bookResponse);

        List<LoanDto> loans = loanService.getActiveLoansByUser("user1");

        assertEquals(1, loans.size());

        LoanDto dto = loans.get(0);

        assertEquals("user1", dto.getUsername());
        assertEquals("Test Book", dto.getBookTitle());
        assertEquals("Test Author", dto.getBookAuthor());
        assertEquals(CategoryName.FICTION, dto.getBookCategory());
        assertEquals(testLoan.getLoanDate().plusDays(15), dto.getDueDate());
        assertFalse(dto.isOverdue()); // 10 dana < 15
    }


}
