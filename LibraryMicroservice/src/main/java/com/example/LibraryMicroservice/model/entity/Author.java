package com.example.LibraryMicroservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(max=30)
    private String name;

    @OneToMany(mappedBy = "author")
    private List<Book> books;


    public Author(String name) {
        this.name = name;
    }
}
