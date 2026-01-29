package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCreateDto {

    @NotBlank(message= "{NotBlank.book.title.notBlank}")
    @Size(max = 30, message = "{Size.book.title.size}")
    private String title;

    private Long authorId;

    @NotBlank(message = "{NotBlank.book.author.notBlank}")
    @Size(max = 30, message = "{Size.book.author.size}")
    private String newAuthorName;



    private String description;   // opis knjige
    private CategoryName category;
    private Integer year;
    private boolean available = true; // default true

}