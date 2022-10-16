package com.example.gomspace.service;

import com.example.gomspace.dto.OperationDto;
import com.example.gomspace.model.Account;
import com.example.gomspace.model.Transaction;
import com.example.gomspace.repos.TransactionRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.gomspace.model.enums.TransactionType.*;

@Service
@AllArgsConstructor
public class TransactionService {
    public final TransactionRepo transactionRepo;

    public Transaction saveWithdrawTransaction(
            final Account account,
            final OperationDto operationDto,
            final double rate) {

        final var transaction = Transaction
                .of(account, operationDto, rate)
                .type(WITHDRAW.name())
                .build();

        return transactionRepo.save(transaction);
    }

    public Transaction saveDepositTransaction(
            final Account account,
            final OperationDto operationDto,
            final double rate) {

        final var transaction = Transaction
                .of(account, operationDto, rate)
                .type(DEPOSIT.name())
                .build();

        return transactionRepo.save(transaction);
    }

    public List<Transaction> saveWireTransferTransaction(
            final Account sourceAccount,
            final Account toAccount,
            final OperationDto operationDto,
            final double sourceRate,
            final double targetRate) {

        final var sourceTransaction = Transaction
                .of(sourceAccount, operationDto, sourceRate)
                .toAccount(toAccount)
                .type(WIRE_TRANSFER.name())
                .build();

        final var targetTransaction = Transaction
                .of(toAccount, operationDto, targetRate)
                .fromAccount(sourceAccount)
                .type(WIRE_TRANSFER.name())
                .build();


        return transactionRepo.saveAll(
                List.of(sourceTransaction, targetTransaction)
        );
    }

    public Page<Transaction> getTransactionsByAccountId(final long accountId, final Pageable pageable) {
        return transactionRepo.findByAccountId(accountId, pageable);
    }

}
