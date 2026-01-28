package com.example.LibraryMicroservice.model.dto;

import com.example.LibraryMicroservice.model.enums.CategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCreateDto {

    @NotBlank
    @Size(max = 30)
    private String title;

    private Long authorId;

    @NotBlank
    @Size(max = 30)
    private String newAuthorName;

    private String description;

    private CategoryName category;

    private Integer year;

    private boolean available = true;

}