package com.example.gomspace.service;

import com.example.gomspace.dto.AccountDto;
import com.example.gomspace.dto.OperationDto;
import com.example.gomspace.model.Account;
import com.example.gomspace.model.ClientException;
import com.example.gomspace.model.Transaction;
import com.example.gomspace.model.enums.AccountType;
import com.example.gomspace.model.enums.Currency;
import com.example.gomspace.repos.AccountRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.gomspace.utils.Constants.SAME_ACCOUNT_ERR;
import static com.example.gomspace.utils.StringTemplates.invalidAccountId;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final ModelMapper modelMapper;

    public Account getAccountById(final long accountId) {
        return accountRepo
                .findById(accountId)
                .orElseThrow(() -> ClientException.forbidden(invalidAccountId(accountId)));
    }

    public Account createAccount(final AccountDto accountDto) {
        Currency.isValidCurrency(accountDto.getCurrency());

        final var account = modelMapper.map(accountDto, Account.class);
        account.setType(AccountType.getAccountType(accountDto.getType()));

        return accountRepo.save(account);
    }

    public Page<Account> getAccountsByOwnerId(final long ownerId, final Pageable pageable) {
        return accountRepo.findByOwnerId(ownerId, pageable);
    }

    public Page<Account> getAllAccounts(final Pageable pageable) {
        return accountRepo.findAll(pageable);
    }

    public Page<Transaction> getAccountHistory(final long accountId, final Pageable pageable) {
        getAccountById(accountId);

        return transactionService.getTransactionsByAccountId(accountId, pageable);
    }

    public Account withdraw(final long accountId, final OperationDto operationDto) {

        final var account = getAccountById(accountId);

        final var rate = currencyService.getCurrencyRate(operationDto.getCurrency(), account.getCurrency());

        account.addAmountToBalance(-rate * operationDto.getAmount());

        transactionService.saveWithdrawTransaction(account, operationDto, rate);

        return accountRepo.save(account);
    }

    public Account deposit(final long accountId, final OperationDto operationDto) {

        final var account = getAccountById(accountId);

        final var rate = currencyService.getCurrencyRate(operationDto.getCurrency(), account.getCurrency());

        account.addAmountToBalance(rate * operationDto.getAmount());

        transactionService.saveDepositTransaction(account, operationDto, rate);

        return accountRepo.save(account);
    }


    public Account wireTransfer(final long accountId, final long targetAccountId, final OperationDto operationDto) {

        if (accountId == targetAccountId) {
            ClientException.throwBadRequest(SAME_ACCOUNT_ERR);
        }


        final var sourceAccount = getAccountById(accountId);
        final var targetAccount = getAccountById(targetAccountId);

        final var sourceRate = currencyService.getCurrencyRate(
                operationDto.getCurrency(), sourceAccount.getCurrency());

        final var targetRate = currencyService.getCurrencyRate(
                targetAccount.getCurrency(), operationDto.getCurrency());

        sourceAccount.addAmountToBalance(-sourceRate * operationDto.getAmount());
        targetAccount.addAmountToBalance(targetRate * operationDto.getAmount());

        transactionService.saveWireTransferTransaction(
                sourceAccount, targetAccount, operationDto, sourceRate, targetRate);

        accountRepo.save(targetAccount);

        return accountRepo.save(sourceAccount);
    }

}


