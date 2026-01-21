package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @Size(max = 30)
    private String title;
    @NotBlank
    @Size(max = 30)
    private String authorName;
    private CategoryName category;
    private int year;
    private boolean available; // true ako nije pozajmljena
    private boolean hasActiveLoans; // true ako je pozajmljena
}

