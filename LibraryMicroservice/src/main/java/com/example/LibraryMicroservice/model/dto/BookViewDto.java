package com.example.LibraryMicroservice.model.dto;

import com.example.LibraryMicroservice.model.enums.CategoryName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookViewDto {
    private Long id;
    private String title;
    private String authorName;
    private String description;
    private CategoryName category;
    private int year;
    private boolean available;
    private boolean hasActiveLoans;
}

