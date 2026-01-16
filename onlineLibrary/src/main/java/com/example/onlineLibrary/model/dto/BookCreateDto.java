package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCreateDto {
    private String title;
    private Long authorId;
    private String newAuthorName; // ime autora iz forme
    private String description;   // opis knjige
    private CategoryName category;
    private Integer year;
    private boolean available = true; // default true

}
