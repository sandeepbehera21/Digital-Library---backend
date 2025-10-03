package com.library.serviceimpl;
import com.library.entity.Book;
import com.library.entity.BookStatus;
import com.library.entity.Transaction;
import com.library.entity.User;
import com.library.exception.BookNotAvailableException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.repository.UserRepository;
import com.library.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(BookRepository bookRepository, UserRepository userRepository, TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction borrowBook(Long bookId, Long userId) {
        // 1. Find the book and user
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 2. Check if the book is available
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BookNotAvailableException("Book with id: " + bookId + " is not available for borrowing.");
        }

        // 3. Update book status and save
        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        // 4. Create and save the transaction
        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setUser(user);
        transaction.setCheckoutDate(LocalDateTime.now());
        transaction.setDueDate(LocalDateTime.now().plusWeeks(2));
        log.info("User {} borrowed book {} (id={})", user.getEmail(), book.getTitle(), bookId);
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction returnBook(Long transactionId) {
        // 1. Find the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        // 2. Get book and update status
        Book book = transaction.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        // 3. Update transaction return date
        transaction.setReturnDate(LocalDateTime.now());
        log.info("Book {} (id={}) returned for transaction {}", book.getTitle(), book.getId(), transactionId);
        return transactionRepository.save(transaction);
    }

    // Pagination methods
    @Override
    public Page<Transaction> pageAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> pageByUser(Long userId, Pageable pageable) {
        return transactionRepository.findByUser_Id(userId, pageable);
    }

    @Override
    public Page<Transaction> pageByBook(Long bookId, Pageable pageable) {
        return transactionRepository.findByBook_Id(bookId, pageable);
    }
}
