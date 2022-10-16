package com.example.gomspace.mock;

import com.example.gomspace.dto.AccountDto;
import com.example.gomspace.dto.OperationDto;
import com.example.gomspace.model.Account;

import static com.example.gomspace.mock.CurrencyMock.VALID_CURRENCY_MOCK;
import static com.example.gomspace.model.enums.AccountType.DEBIT;
import static com.example.gomspace.model.enums.Currency.EUR;

public interface AccountMock {
    long VALID_OWNER_ID = 1L;
    long VALID_ACCOUNT_ID = 1L;

    static AccountDto mockAccountDto() {
        return AccountDto.builder()
                .ownerId(VALID_OWNER_ID)
                .currency(VALID_CURRENCY_MOCK)
                .type(DEBIT.name())
                .build();
    }

    static Account mockAccount() {
        return Account.builder()
                .id(VALID_ACCOUNT_ID)
                .ownerId(VALID_OWNER_ID)
                .balance(0)
                .currency(VALID_CURRENCY_MOCK)
                .type(DEBIT)
                .build();
    }

    static OperationDto mockOperationDto() {
        return new OperationDto(0D, EUR.name());
    }
}
