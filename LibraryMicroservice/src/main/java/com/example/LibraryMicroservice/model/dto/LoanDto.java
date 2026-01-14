package com.example.LibraryMicroservice.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanDto {

    private Long loanId;
    private Long bookId;
    private String bookTitle;    // opcionalno, može biti "Unknown" ako nema
    private String bookAuthor;   // opcionalno
    private String bookCategory; // opcionalno
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
}
