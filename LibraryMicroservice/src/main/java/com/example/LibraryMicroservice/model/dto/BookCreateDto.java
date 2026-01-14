package com.example.LibraryMicroservice.model.dto;


import com.example.LibraryMicroservice.model.enums.CategoryName;
import lombok.*;

@Getter
@Setter
public class BookCreateDto {
    private String title;
    private String newAuthorName; // ime autora iz forme
    private String description;   // opis knjige
    private CategoryName category;
    private Integer year;
    private boolean available = true; // default true

}
