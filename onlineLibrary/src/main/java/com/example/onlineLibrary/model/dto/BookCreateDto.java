package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCreateDto {
    private String title;
    private String newAuthorName; // ime autora iz forme
    private String description;   // opis knjige
    private CategoryName category;
    private Integer year;
    private boolean available = true; // default true

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getAuthorName() {
        return newAuthorName;
    }
}
