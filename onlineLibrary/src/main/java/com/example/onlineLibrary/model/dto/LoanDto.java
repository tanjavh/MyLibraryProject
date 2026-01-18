package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    private Long loanId;
    private Long bookId;
    private String bookTitle;
    // 🟢 Dodaj ovo
    private String bookAuthor;
    private CategoryName bookCategory;
    private LocalDate loanDate;
    private LocalDate dueDate;      // ➤ datum kada je knjiga trebala biti vraćena
    private LocalDate returnDate;
    private boolean returned;
    private boolean overdue;        // za upozorenje 15-30 dana

    // informacije o korisniku
    private Long userId;
    private String username;
}
