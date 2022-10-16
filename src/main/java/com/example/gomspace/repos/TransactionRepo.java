package com.example.gomspace.repos;

import com.example.gomspace.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountId(final long accountId, final Pageable pageable);
}
