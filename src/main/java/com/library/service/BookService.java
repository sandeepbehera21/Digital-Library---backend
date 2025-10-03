package com.library.service;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    List<Book> findAllBooks();
    List<Book> findAvailableBooks();
    Book findBookById(Long id);
    Book addBook(Book book);
    void deleteBook(Long id);

    //  New search methods
    List<Book> searchBooksByTitle(String title);
    List<Book> searchBooksByAuthor(String author);

    // Pagination/Sorting
    Page<Book> pageAllBooks(Pageable pageable);
    Page<Book> searchBooksByTitle(String title, Pageable pageable);
    Page<Book> searchBooksByAuthor(String author, Pageable pageable);
}
