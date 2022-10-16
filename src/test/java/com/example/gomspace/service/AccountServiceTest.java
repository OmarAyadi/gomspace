package com.example.gomspace.service;

import com.example.gomspace.dto.OperationDto;
import com.example.gomspace.model.Account;
import com.example.gomspace.model.ClientException;
import com.example.gomspace.model.enums.Currency;
import com.example.gomspace.repos.AccountRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;

import static com.example.gomspace.mock.AccountMock.mockAccount;
import static com.example.gomspace.mock.AccountMock.mockAccountDto;
import static com.example.gomspace.model.enums.Currency.EUR;
import static com.example.gomspace.repo.UtilMock.VALID_PAGINATION_MOCK;
import static com.example.gomspace.utils.Constants.*;
import static com.example.gomspace.utils.StringTemplates.invalidAccountId;
import static com.example.gomspace.utils.StringTemplates.invalidValue;
import static com.example.gomspace.utils.Utils.getAndSortKeys;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class AccountServiceTest {
    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private TransactionService transactionService;

    private AccountRepo accountRepo;
    private AccountService accountService;

    @Autowired
    AccountServiceTest(CurrencyService currencyService, TransactionService transactionService, AccountRepo accountRepo) {
        this.currencyService = currencyService;
        this.transactionService = transactionService;
        this.accountRepo = accountRepo;
        this.accountService = new AccountService(
                accountRepo,
                this.transactionService,
                this.currencyService,
                new ModelMapper()
        );
    }

    @AfterEach
    public void cleanUp() {
        accountRepo.deleteAll();
    }

    public Account createAccountWithBalance(double balance) {
        assertTrue(balance >= 0);

        final var account = mockAccount();
        account.setId(Instant.now().getEpochSecond());
        account.setBalance(balance);
        return accountRepo.save(account);
    }

    @Nested
    class GetAccountByIdTests {

        @DisplayName("invalid account id, not found in database")
        @Test
        public void throwErrorInvalidAccountId() {
            final var invalidId = 0L;

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.getAccountById(invalidId));

            assertEquals(
                    invalidAccountId(invalidId),
                    clientException.getReason()
            );

        }

        @DisplayName("should return valid account")
        @Test
        public void returnAccountById() {
            final var validAccount = accountRepo.save(mockAccount());

            final var account = accountService.getAccountById(validAccount.getId());

            assertEquals(validAccount, account);
        }
    }

    @Nested
    class CreateAccountTests {

        @DisplayName("throw error, invalid currency")
        @Test
        public void throwErrorInvalidCurrency() {
            final var invalidCurrency = "test";
            final var accountDto = mockAccountDto();
            accountDto.setCurrency(invalidCurrency);

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.createAccount(accountDto));

            assertEquals(
                    invalidValue(invalidCurrency, CURRENCY, getAndSortKeys(Currency.possibleCurrencies)),
                    clientException.getReason()
            );
        }

        @DisplayName("valid account creation")
        @Test
        public void createAnAccount() {
            final var accountDto = mockAccountDto();

            final var account = accountService.createAccount(accountDto);
            assertNotNull(account.getId());
        }
    }

    @Nested
    class GetAccountByOwnerId {
        @DisplayName("no accounts found for the given owner id")
        @Test
        public void returnEmptyAccountList() {
            final var account = mockAccount();

            final var accounts = accountService
                    .getAccountsByOwnerId(account.getOwnerId(), VALID_PAGINATION_MOCK);

            assertThat(accounts).hasSize(0);

        }


        @DisplayName("an account is found for the given owner id")
        @Test
        public void returnValidAccountsForOwner() {
            final var account = mockAccount();
            accountRepo.save(account);

            final var accounts = accountService
                    .getAccountsByOwnerId(account.getOwnerId(), VALID_PAGINATION_MOCK);

            assertThat(accounts).hasSize(1);
        }
    }

    @Nested
    class GetAllAccounts {

        @DisplayName("return accounts")
        @Test
        public void returnValidAccounts() {
            accountRepo.save(mockAccount());

            final var accounts = accountService.getAllAccounts(VALID_PAGINATION_MOCK);

            assertThat(accounts).hasSize(1);
        }
    }

    @Nested
    class WithdrawTests {

        @DisplayName("invalid account id, not found in database")
        @Test
        public void throwErrorInvalidAccountId() {
            final var invalidId = 0L;

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.withdraw(invalidId, new OperationDto()));

            assertEquals(
                    invalidAccountId(invalidId),
                    clientException.getReason()
            );
        }

        @DisplayName("throw error because the account dose not contain enough balance")
        @Test
        public void throwErrorInsufficientBalance() {
            final var account = accountService.createAccount(mockAccountDto());

            final var withdrawOperation = new OperationDto(10D, EUR.name());

            when(currencyService.getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency()))
                    .thenReturn(1D);

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.withdraw(account.getId(), withdrawOperation));

            assertEquals(
                    INSUFFICIENT_BALANCE,
                    clientException.getReason()
            );

            verify(currencyService, times(1))
                    .getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency());
        }

        @DisplayName("throw error because the account dose not contain enough balance because of the conversion")
        @Test
        public void throwErrorInsufficientBalanceBecauseOfTheConversion() {
            final var account = createAccountWithBalance(10D);

            final var depositOperation = new OperationDto(10D, account.getCurrency());
            accountService.deposit(account.getId(), depositOperation);


            final var withdrawOperation = new OperationDto(10D, EUR.name());

            when(currencyService.getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency()))
                    .thenReturn(1.1D);

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.withdraw(account.getId(), withdrawOperation));

            assertEquals(
                    INSUFFICIENT_BALANCE,
                    clientException.getReason()
            );

            verify(currencyService, times(1))
                    .getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency());
        }

        @DisplayName("calculation a withdraw for the same currency")
        @Test
        public void calculateWithdrawForSameCurrency() {
            final var account = createAccountWithBalance(10);

            final var withdrawOperation = new OperationDto(1D, account.getCurrency());

            when(currencyService.getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency()))
                    .thenReturn(1D);

            final var updatedAccount = accountService.withdraw(account.getId(), withdrawOperation);

            assertEquals(9, updatedAccount.getBalance());

            verify(currencyService, times(1))
                    .getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency());
        }

        @DisplayName("calculation a withdraw for the different currency")
        @Test
        public void calculateWithdrawForDifferentCurrency() {
            final var account = createAccountWithBalance(10);

            final var withdrawOperation = new OperationDto(1D, EUR.name());

            when(currencyService.getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency()))
                    .thenReturn(2D);

            final var updatedAccount = accountService.withdraw(account.getId(), withdrawOperation);

            assertEquals(8, updatedAccount.getBalance());

            verify(currencyService, times(1))
                    .getCurrencyRate(withdrawOperation.getCurrency(), account.getCurrency());
        }
    }

    @Nested
    class DepositTests {

        @DisplayName("invalid account id, not found in database")
        @Test
        public void throwErrorInvalidAccountId() {
            final var invalidId = 0L;

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.deposit(invalidId, new OperationDto()));

            assertEquals(
                    invalidAccountId(invalidId),
                    clientException.getReason()
            );
        }

        @DisplayName("calculation a deposit for the same currency")
        @Test
        public void calculateDepositForSameCurrency() {
            final var account = createAccountWithBalance(0);

            final var depositOperation = new OperationDto(1D, account.getCurrency());

            when(currencyService.getCurrencyRate(depositOperation.getCurrency(), account.getCurrency()))
                    .thenReturn(1D);

            final var updatedAccount = accountService.deposit(account.getId(), depositOperation);

            assertEquals(1, updatedAccount.getBalance());

            verify(currencyService, times(1))
                    .getCurrencyRate(depositOperation.getCurrency(), account.getCurrency());
        }


        @DisplayName("calculation a deposit for the different currency")
        @Test
        public void calculateDepositForDifferentCurrency() {
            final var account = createAccountWithBalance(0);

            final var depositOperation = new OperationDto(1D, EUR.name());

            when(currencyService.getCurrencyRate(depositOperation.getCurrency(), account.getCurrency()))
                    .thenReturn(2D);

            final var updatedAccount = accountService.deposit(account.getId(), depositOperation);

            assertEquals(2, updatedAccount.getBalance());

            verify(currencyService, times(1))
                    .getCurrencyRate(depositOperation.getCurrency(), account.getCurrency());
        }
    }

    @Nested
    class WireTransferTests {

        @DisplayName("invalid account id, not found in database")
        @Test
        public void throwErrorInvalidAccountId() {
            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.wireTransfer(0L, 1L, new OperationDto()));

            assertEquals(
                    invalidAccountId(0L),
                    clientException.getReason()
            );
        }

        @DisplayName("invalid to account id, not found in database")
        @Test
        public void throwErrorInvalidToAccountId() {
            final var account = createAccountWithBalance(0);
            final var invalidId = 0L;

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.wireTransfer(account.getId(), invalidId, new OperationDto()));

            assertEquals(
                    invalidAccountId(invalidId),
                    clientException.getReason()
            );
        }

        @DisplayName("throw error since the user provided same id for account and to account")
        @Test
        public void throwErrorSameAccount() {
            final var account = createAccountWithBalance(0);

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.wireTransfer(account.getId(), account.getId(), new OperationDto()));

            assertEquals(
                    SAME_ACCOUNT_ERR,
                    clientException.getReason()
            );
        }

        @DisplayName("throw error because the account dose not contain enough balance")
        @Test
        public void throwErrorInsufficientBalance() {
            final var account = createAccountWithBalance(0);
            final var toAccount = createAccountWithBalance(0);

            final var wireTransferOp = new OperationDto(10D, account.getCurrency());

            when(currencyService.getCurrencyRate(wireTransferOp.getCurrency(), account.getCurrency()))
                    .thenReturn(1D);
            when(currencyService.getCurrencyRate(toAccount.getCurrency(), wireTransferOp.getCurrency()))
                    .thenReturn(1D);

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.wireTransfer(account.getId(), toAccount.getId(), wireTransferOp));

            assertEquals(
                    INSUFFICIENT_BALANCE,
                    clientException.getReason()
            );

            verify(currencyService, times(2))
                    .getCurrencyRate(account.getCurrency(), account.getCurrency());
        }

        @DisplayName("throw error because the account dose not contain enough balance because of the conversion")
        @Test
        public void throwErrorInsufficientBalanceBecauseOfTheConversion() {
            final var account = createAccountWithBalance(10);
            final var toAccount = createAccountWithBalance(0);

            final var wireTransferOp = new OperationDto(10D, EUR.name());

            when(currencyService.getCurrencyRate(wireTransferOp.getCurrency(), account.getCurrency()))
                    .thenReturn(2D);
            when(currencyService.getCurrencyRate(toAccount.getCurrency(), wireTransferOp.getCurrency()))
                    .thenReturn(1D);

            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> accountService.wireTransfer(account.getId(), toAccount.getId(), wireTransferOp));

            assertEquals(
                    INSUFFICIENT_BALANCE,
                    clientException.getReason()
            );

            verify(currencyService, times(1))
                    .getCurrencyRate(wireTransferOp.getCurrency(), account.getCurrency());
            verify(currencyService, times(1))
                    .getCurrencyRate(toAccount.getCurrency(), wireTransferOp.getCurrency());
        }

        @DisplayName("calculation a wire transfer for the same currency")
        @Test
        public void calculateWithdrawForSameCurrency() {
            final var account = createAccountWithBalance(10);
            final var toAccount = createAccountWithBalance(0);

            final var wireTransferOp = new OperationDto(10D, account.getCurrency());

            when(currencyService.getCurrencyRate(wireTransferOp.getCurrency(), account.getCurrency()))
                    .thenReturn(1D);
            when(currencyService.getCurrencyRate(toAccount.getCurrency(), wireTransferOp.getCurrency()))
                    .thenReturn(1D);

            final var updatedAccount = accountService.wireTransfer(account.getId(), toAccount.getId(), wireTransferOp);
            assertEquals(0D, updatedAccount.getBalance());

            final var updatedToAccount = accountRepo.findById(toAccount.getId()).get();
            assertEquals(10D, updatedToAccount.getBalance());


            verify(currencyService, times(2))
                    .getCurrencyRate(account.getCurrency(), account.getCurrency());
        }


        @DisplayName("calculation a withdraw for the different currency")
        @Test
        public void calculateWithdrawForDifferentCurrency() {
            final var account = createAccountWithBalance(10);
            final var toAccount = createAccountWithBalance(0);

            final var wireTransferOp = new OperationDto(1D, EUR.name());

            when(currencyService.getCurrencyRate(wireTransferOp.getCurrency(), account.getCurrency()))
                    .thenReturn(2D);
            when(currencyService.getCurrencyRate(toAccount.getCurrency(), wireTransferOp.getCurrency()))
                    .thenReturn(1D);

            final var updatedAccount = accountService.wireTransfer(account.getId(), toAccount.getId(), wireTransferOp);
            assertEquals(8D, updatedAccount.getBalance());

            final var updatedToAccount = accountRepo.findById(toAccount.getId()).get();
            assertEquals(1D, updatedToAccount.getBalance());

            verify(currencyService, times(1))
                    .getCurrencyRate(wireTransferOp.getCurrency(), account.getCurrency());
            verify(currencyService, times(1))
                    .getCurrencyRate(toAccount.getCurrency(), wireTransferOp.getCurrency());
        }
    }

}
