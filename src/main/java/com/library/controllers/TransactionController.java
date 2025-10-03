package com.library.controllers;

import com.library.entity.Transaction;
import com.library.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<Transaction> borrowBook(@RequestParam Long bookId, @RequestParam Long userId) {
        Transaction transaction = transactionService.borrowBook(bookId, userId);
        return ResponseEntity.ok(transaction);
    }

    // Return a book
    @PostMapping("/return/{transactionId}")
    public ResponseEntity<Transaction> returnBook(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.returnBook(transactionId);
        return ResponseEntity.ok(transaction);
    }

    // Page all transactions
    @GetMapping
    public ResponseEntity<Page<Transaction>> pageAll(Pageable pageable) {
        return ResponseEntity.ok(transactionService.pageAll(pageable));
    }

    // Page by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Transaction>> pageByUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(transactionService.pageByUser(userId, pageable));
    }

    // Page by book
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<Transaction>> pageByBook(@PathVariable Long bookId, Pageable pageable) {
        return ResponseEntity.ok(transactionService.pageByBook(bookId, pageable));
    }
}