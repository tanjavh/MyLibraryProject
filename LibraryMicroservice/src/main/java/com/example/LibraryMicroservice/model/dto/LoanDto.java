package com.example.LibraryMicroservice.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanDto {

    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCategory;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
}
