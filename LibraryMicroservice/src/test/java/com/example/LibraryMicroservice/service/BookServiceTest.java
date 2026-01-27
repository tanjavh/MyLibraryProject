package com.example.LibraryMicroservice.service;

import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.entity.Author;
import com.example.LibraryMicroservice.model.entity.Book;
import com.example.LibraryMicroservice.model.enums.CategoryName;
import com.example.LibraryMicroservice.repository.AuthorRepository;
import com.example.LibraryMicroservice.repository.BookRepository;
import com.example.LibraryMicroservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional    // ⬅️ koristi application-test.properties         // ⬅️ rollback posle svakog testa
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        //bookRepository.deleteAll();
        categoryRepository.deleteAll();
        //authorRepository.deleteAll();
    }

    @Test
    void testCreateBook_withNewAuthor_createsAuthorOnce() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("Test Book");
        dto.setNewAuthorName("Ivo Andrić");
        dto.setCategory(CategoryName.CLASSIC);
        dto.setYear(1950);

        Book book = bookService.createFromDto(dto);

        assertNotNull(book.getId());
        assertNotNull(book.getAuthor());

        List<Author> authors = authorRepository.findAll();
        assertEquals(1, authors.size());
        assertEquals("Ivo Andrić", authors.get(0).getName());
    }

    @Test
    void testCreateTwoBooks_sameAuthorName_doesNotDuplicateAuthor() {
        BookCreateDto dto1 = new BookCreateDto();
        dto1.setTitle("Book 1");
        dto1.setNewAuthorName("Mesa Selimovic");
        dto1.setCategory(CategoryName.CLASSIC);
        dto1.setYear(1966);

        BookCreateDto dto2 = new BookCreateDto();
        dto2.setTitle("Book 2");
        dto2.setNewAuthorName("Mesa Selimovic");
        dto2.setCategory(CategoryName.CLASSIC);
        dto2.setYear(1970);

        bookService.createFromDto(dto1);
        bookService.createFromDto(dto2);

        List<Author> authors = authorRepository.findAll();
        List<Book> books = bookRepository.findAll();

        assertEquals(1, authors.size(), "Autor ne sme biti dupliran");
        assertEquals(2, books.size());
        assertEquals(authors.get(0).getId(), books.get(0).getAuthor().getId());
    }


    @Test
    void testCreateBook_withoutAuthor_throwsException() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("Invalid Book");
        dto.setCategory(CategoryName.CLASSIC);
        dto.setYear(2000);

        assertThrows(RuntimeException.class,
                () -> bookService.createFromDto(dto));
    }

    @Test
    void testBookIsAvailableByDefault() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("Available Book");
        dto.setNewAuthorName("Test Author");
        dto.setCategory(CategoryName.FICTION);
        dto.setYear(2020);

        Book book = bookService.createFromDto(dto);

        assertTrue(book.isAvailable());
    }
}

