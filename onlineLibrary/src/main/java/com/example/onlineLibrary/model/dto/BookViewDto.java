package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookViewDto {
    private Long id;
    private String title;
    private String authorName;
    private CategoryName category;
    private int year;
    private boolean available; // true ako nije pozajmljena
    private boolean hasActiveLoans; // true ako je pozajmljena
}

