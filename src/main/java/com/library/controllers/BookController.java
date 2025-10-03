package com.library.controllers;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.entity.Book;
import com.library.entity.BookStatus;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Create
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest req) {
        Book book = new Book();
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setPublicationYear(req.getPublicationYear());
        book.setStatus(BookStatus.AVAILABLE);
        Book saved = bookService.addBook(book);
        BookResponse res = toResponse(saved);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    // Read by id
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        Book book = bookService.findBookById(id);
        return ResponseEntity.ok(toResponse(book));
    }

    // List all with pagination/sorting
    @GetMapping
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookService.pageAllBooks(pageable).map(this::toResponse);
    }

    // Available only (no paging for simplicity)
    @GetMapping("/available")
    public List<BookResponse> getAvailableBooks() {
        return bookService.findAvailableBooks().stream().map(this::toResponse).toList();
    }

    // Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Search by title with paging
    @GetMapping("/search/title")
    public Page<BookResponse> searchBooksByTitle(@RequestParam String query, Pageable pageable) {
        return bookService.searchBooksByTitle(query, pageable).map(this::toResponse);
    }

    // Search by author with paging
    @GetMapping("/search/author")
    public Page<BookResponse> searchBooksByAuthor(@RequestParam String query, Pageable pageable) {
        return bookService.searchBooksByAuthor(query, pageable).map(this::toResponse);
    }

    private BookResponse toResponse(Book b) {
        BookResponse r = new BookResponse();
        r.setId(b.getId());
        r.setTitle(b.getTitle());
        r.setAuthor(b.getAuthor());
        r.setIsbn(b.getIsbn());
        r.setPublicationYear(b.getPublicationYear());
        r.setStatus(b.getStatus());
        return r;
    }
}