package com.library.serviceimpl;

import com.library.entity.Book;
import com.library.entity.BookStatus;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void findBookById_whenBookExists_shouldReturnBook() {
        // Given
        Book book = new Book(1L, "Test Title", "Test Author", "12345", 2025, BookStatus.AVAILABLE, null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // When
        Book foundBook = bookService.findBookById(1L);

        // Then
        assertEquals("Test Title", foundBook.getTitle());
        assertEquals("Test Author", foundBook.getAuthor());
    }

    @Test
    void findBookById_whenBookDoesNotExist_shouldThrowException() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookService.findBookById(1L));
    }
}
