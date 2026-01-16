package com.example.onlineLibrary.model.dto;

import com.example.onlineLibrary.model.enums.CategoryName;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor // ovo omogućava new BookInfoResponse()
@AllArgsConstructor
public class BookInfoResponse {
    private Long id;
    private String title;
    private String authorName;
    private CategoryName category;
    private int year;
    private boolean available;
}

