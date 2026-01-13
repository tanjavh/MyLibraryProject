package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookInfoResponse {
    private Long id;
    private String title;
    private String authorName;
    private CategoryName category;
    private int year;
    private boolean available;
}

