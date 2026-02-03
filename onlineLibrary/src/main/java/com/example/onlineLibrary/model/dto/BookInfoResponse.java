package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor // ovo omogućava new BookInfoResponse()
@AllArgsConstructor
public class BookInfoResponse {

    private Long id;

    @NotBlank
    @Size(max = 30)
    private String title;

    @NotBlank
    @Size(max = 30)
    private String authorName;

    private CategoryName category;

    private int year;

    private boolean available;

    private boolean hasActiveLoans; // za kontrolu dugmeta Obriši


    public boolean borrowedByCurrentUser;
}