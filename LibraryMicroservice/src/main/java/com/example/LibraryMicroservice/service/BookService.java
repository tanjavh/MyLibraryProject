package com.example.LibraryMicroservice.service;

import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.dto.BookInfoResponse;
import com.example.LibraryMicroservice.model.entity.Author;
import com.example.LibraryMicroservice.model.entity.Book;
import com.example.LibraryMicroservice.model.entity.Category;
import com.example.LibraryMicroservice.repository.AuthorRepository;
import com.example.LibraryMicroservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final AuthorRepository authorRepository;


    public List<Book> findAll() {
        return bookRepository.findAll();
    }


//    public Optional<Book> findById(Long id) {
//        return bookRepository.findById(id);
//    }


    public Book save(Book book) {
        return bookRepository.save(book);
    }


    public void delete(Book book) {
        bookRepository.delete(book);
    }


    @Transactional
    public void deleteById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena"));

        Author author = book.getAuthor();

        bookRepository.delete(book);

        bookRepository.flush();

        if (author.getBooks().isEmpty()) {
            authorRepository.delete(author);
        }
    }


    @Transactional
    public Book createFromDto(BookCreateDto dto) {

        Author author;

        if (dto.getNewAuthorName() != null && !dto.getNewAuthorName().isBlank()) {

            String authorName = dto.getNewAuthorName().trim();

            author = authorService.findByName(authorName)
                    .orElseGet(() -> {
                        Author newAuthor = new Author();
                        newAuthor.setName(authorName);
                        return authorService.save(newAuthor);
                    });

        } else if (dto.getAuthorId() != null) {

            author = authorService.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Autor ne postoji!"));

        } else {
            throw new RuntimeException("Morate uneti autora!");
        }

        Category category = categoryService.findByName(dto.getCategory())
                .orElseGet(() -> categoryService.save(new Category(dto.getCategory())));

        // ⚠️ ModelMapper koristi se samo za prosta polja
        Book book = modelMapper.map(dto, Book.class);

        // relacije ručno
        book.setAuthor(author);
        book.setCategory(category);
        book.setAvailable(true);

        return bookRepository.save(book);
    }

    public BookInfoResponse toBookInfo(Book book) {
        return BookInfoResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthor().getName())
                .category(book.getCategory().getName()) // enum
                .year(book.getYear())
                .available(book.isAvailable())
                .build();
    }

    public void updateAvailability(Long bookId, boolean available) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Knjiga nije pronađena"));
        book.setAvailable(available);
        bookRepository.save(book);
    }


    public BookInfoResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Knjiga nije pronađena"
                ));

        return BookInfoResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthor().getName())
                .category(book.getCategory().getName())
                .year(book.getYear())
                .available(book.isAvailable())
                .build();
    }
    public List<Book> findAllSorted(String sort) {
        if ("title".equalsIgnoreCase(sort)) {
            return bookRepository.findAllOrderByTitle();
        }

        if ("author".equalsIgnoreCase(sort)) {
            return bookRepository.findAllOrderByAuthor();
        }

        // default ponašanje – kao do sada
        return bookRepository.findAll();
    }
}