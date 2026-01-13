package com.example.LibraryMicroservice.model.entity;


import com.example.LibraryMicroservice.model.enums.CategoryName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CategoryName name;

    public Category(CategoryName name) {
        this.name = name;
    }


}
