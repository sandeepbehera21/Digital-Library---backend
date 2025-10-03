package com.library.service;

import com.library.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Transaction borrowBook(Long bookId, Long userId);
    Transaction returnBook(Long transactionId);

    // Pagination / Sorting
    Page<Transaction> pageAll(Pageable pageable);
    Page<Transaction> pageByUser(Long userId, Pageable pageable);
    Page<Transaction> pageByBook(Long bookId, Pageable pageable);
}