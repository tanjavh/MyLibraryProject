package com.example.LibraryMicroservice.repository;

import com.example.LibraryMicroservice.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);

    boolean existsByTitle(String title);

    // SORTIRANJE PO NASLOVU
    @Query("SELECT b FROM Book b ORDER BY b.title ASC")
    List<Book> findAllOrderByTitle();

    // SORTIRANJE PO AUTORU
    @Query("SELECT b FROM Book b ORDER BY b.author.name ASC")
    List<Book> findAllOrderByAuthor();
}
