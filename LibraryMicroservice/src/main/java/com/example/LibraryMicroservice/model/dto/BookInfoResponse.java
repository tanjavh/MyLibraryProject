package com.example.LibraryMicroservice.model.dto;


import com.example.LibraryMicroservice.model.enums.CategoryName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInfoResponse {
    private Long id;
    private String title;
    private String authorName;
    private CategoryName category;
    private int year;
    private boolean available;
}

