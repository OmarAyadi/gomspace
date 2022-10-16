package com.example.gomspace.controller;

import com.example.gomspace.dto.AccountDto;
import com.example.gomspace.dto.OperationDto;
import com.example.gomspace.model.Account;
import com.example.gomspace.model.Transaction;
import com.example.gomspace.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.gomspace.model.ClientException.handleValidationErrors;

@RestController
@RequestMapping("/api/v1/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(
            final @RequestBody @Valid AccountDto accountDto,
            final Errors errors) {

        handleValidationErrors(errors);

        final var account = accountService.createAccount(accountDto);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<Page<Account>> getAllAccounts(final Pageable pageable) {
        final var accounts = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(final @PathVariable Long accountId) {

        final var transactions = accountService.getAccountById(accountId);
        return ResponseEntity.ok(transactions);
    }


    @GetMapping("/{accountId}/history")
    public ResponseEntity<Page<Transaction>> getAccountHistory(
            final @PathVariable Long accountId,
            final Pageable pageable) {

        final var transactions = accountService.getAccountHistory(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/owners/{ownerId}")
    public ResponseEntity<Page<Account>> getAccountsByOwnerId(
            final @PathVariable Long ownerId, final Pageable pageable) {

        final var accounts = accountService.getAccountsByOwnerId(ownerId, pageable);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}/withdraw")
    public ResponseEntity<Account> withdraw(
            final @PathVariable Long accountId,
            final @RequestBody @Valid OperationDto operationDto,
            final Errors errors) {

        handleValidationErrors(errors);

        final var account = accountService.withdraw(accountId, operationDto);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}/deposit")
    public ResponseEntity<Account> deposit(
            final @PathVariable Long accountId,
            final @RequestBody @Valid OperationDto operationDto,
            final Errors errors) {

        handleValidationErrors(errors);

        final var account = accountService.deposit(accountId, operationDto);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}/wire-transfer/{targetAccountId}")
    public ResponseEntity<Account> wireTransfer(
            final @PathVariable Long accountId,
            final @PathVariable Long targetAccountId,
            final @RequestBody @Valid OperationDto operationDto,
            final Errors errors) {

        handleValidationErrors(errors);

        final var account = accountService.wireTransfer(accountId, targetAccountId, operationDto);
        return ResponseEntity.ok(account);
    }
}
