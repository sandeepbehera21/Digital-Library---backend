package com.library.repository;

import com.library.entity.Book;
import com.library.entity.BookStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void whenFindByTitleContainingIgnoreCase_thenReturnBook() {
        // Given
        Book book = new Book(null, "Test Title", "Test Author", "12345", 2025, BookStatus.AVAILABLE, null);
        bookRepository.save(book);

        // When
        List<Book> found = bookRepository.findByTitleContainingIgnoreCase("test title");

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getAuthor()).isEqualTo("Test Author");
    }
}
