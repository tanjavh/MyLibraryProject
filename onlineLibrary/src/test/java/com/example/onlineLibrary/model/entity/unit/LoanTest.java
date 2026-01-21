package com.example.onlineLibrary.model.entity.unit;

import com.example.onlineLibrary.model.entity.Loan;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LoanTest {
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
