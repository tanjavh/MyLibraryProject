package com.example.LibraryMicroservice.model.dto;

import com.example.LibraryMicroservice.model.enums.CategoryName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCreateDto {
    private String title;

    // za postojeće autore iz selecta
    private Long authorId;

    // za novog autora
    private String newAuthorName;

    private CategoryName category;

    private Integer year;
}
