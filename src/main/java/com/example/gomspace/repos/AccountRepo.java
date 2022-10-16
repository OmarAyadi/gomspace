package com.example.gomspace.repos;

import com.example.gomspace.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account,Long> {
    Page<Account> findByOwnerId(final long ownerId, final Pageable pageable);
}
