package com.example.LibraryMicroservice.service;



import com.example.LibraryMicroservice.model.dto.BookCreateDto;
import com.example.LibraryMicroservice.model.dto.BookInfoResponse;
import com.example.LibraryMicroservice.model.entity.Author;
import com.example.LibraryMicroservice.model.entity.Book;
import com.example.LibraryMicroservice.model.entity.Category;
import com.example.LibraryMicroservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    // Dohvata sve knjige
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    // Dohvata knjigu po ID-ju
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    // Čuvanje knjige
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    // Brisanje knjige po objektu
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    // Brisanje knjige po ID-ju
    public void deleteById(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    public Book createFromDto(BookCreateDto dto) {

        Author author;

        if (dto.getNewAuthorName() != null && !dto.getNewAuthorName().isBlank()) {
            author = new Author();
            author.setName(dto.getNewAuthorName());
            author = authorService.save(author);
        } else if (dto.getAuthorId() != null) {
            author = authorService.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Izabrani autor ne postoji!"));
        } else {
            throw new RuntimeException("Morate uneti autora!");
        }

        Category category = categoryService.findByName(dto.getCategory())
                .orElseGet(() -> categoryService.save(new Category(dto.getCategory())));

        // ✅ ModelMapper radi samo mapiranje
        Book book = modelMapper.map(dto, Book.class);

        // relacije se postavljaju ručno
        book.setAuthor(author);
        book.setCategory(category);

        // dostupnost je poslovno pravilo mikroservisa
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
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setAvailable(available);
        bookRepository.save(book);
    }


    public BookInfoResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"
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
}