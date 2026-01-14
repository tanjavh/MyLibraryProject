package com.example.onlineLibrary.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class LoanDto {

    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCategory;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
    private String username;



}
