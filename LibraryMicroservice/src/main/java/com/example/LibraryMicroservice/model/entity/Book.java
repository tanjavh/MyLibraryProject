package com.example.LibraryMicroservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(optional = false)
    private Author author;

    @ManyToOne(optional = false)
    private Category category;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private boolean available;
}
