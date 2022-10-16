package com.example.gomspace.controller;

import com.example.gomspace.model.ClientException;
import com.example.gomspace.service.AccountService;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.gomspace.mock.AccountMock.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@ExtendWith(SpringExtension.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private final static Gson jsonConvertor = new Gson();

    @Nested
    class GetAccountByIdTests {

        @DisplayName("throw error, id is string")
        @Test
        public void throwErrorIdIsString() throws Exception {

            mockMvc.perform(get("/api/v1/accounts/{accountId}", "test"))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("invalid account id, not found in database")
        @Test
        public void throwErrorInvalidAccountId() throws Exception {

            when(accountService.getAccountById(0L))
                    .thenThrow(ClientException.forbidden(""));

            mockMvc.perform(get("/api/v1/accounts/{accountId}", 0L))
                    .andExpect(status().isForbidden());

            verify(accountService, times(1)).getAccountById(0L);
        }

        @DisplayName("should return valid account")
        @Test
        public void returnAccountById() throws Exception {

            final var account = mockAccount();
            when(accountService.getAccountById(1L))
                    .thenReturn(account);

            mockMvc.perform(get("/api/v1/accounts/{accountId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(account.getId()));

            verify(accountService, times(1)).getAccountById(1L);
        }
    }

    @Nested
    class CreateAccountTests {

        @DisplayName("throw error, currency cannot be null")
        @Test
        public void throwErrorCurrencyIsNull() throws Exception {
            final var accountDto = mockAccountDto();
            accountDto.setCurrency(null);

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(accountDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency cannot be blank")
        @Test
        public void throwErrorCurrencyIsBlank() throws Exception {
            final var accountDto = mockAccountDto();
            accountDto.setCurrency("");

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(accountDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, type cannot be null")
        @Test
        public void throwErrorTypeIsNull() throws Exception {
            final var accountDto = mockAccountDto();
            accountDto.setType(null);

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(accountDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, type cannot be blank")
        @Test
        public void throwErrorTypeIsBlank() throws Exception {
            final var accountDto = mockAccountDto();
            accountDto.setCurrency("");

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(accountDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, owner id be null")
        @Test
        public void throwErrorOwnerIdIsNull() throws Exception {
            final var accountDto = mockAccountDto();
            accountDto.setOwnerId(null);

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(accountDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("valid account creation")
        @Test
        public void returnAccountById() throws Exception {
            final var accountDto = mockAccountDto();
            final var account = mockAccount();
            when(accountService.createAccount(accountDto))
                    .thenReturn(account);

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(accountDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(account.getId()));

            verify(accountService, times(1))
                    .createAccount(accountDto);
        }
    }

    @Nested
    class GetAccountByOwnerId {

        @DisplayName("throw error, id is string")
        @Test
        public void throwErrorIdIsString() throws Exception {

            mockMvc.perform(get("/api/v1/accounts/owners/{ownerId}", "test"))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("no accounts found for the given owner id")
        @Test
        public void returnEmptyAccountList() throws Exception {
            when(accountService.getAccountsByOwnerId(0L, PageRequest.of(0, 20)))
                    .thenReturn(Page.empty());

            mockMvc.perform(get("/api/v1/accounts/owners/{ownerId}", 0L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());

            verify(accountService, times(1))
                    .getAccountsByOwnerId(0L, PageRequest.of(0, 20));

        }


        @DisplayName("an account is found for the given owner id")
        @Test
        public void returnValidAccountsForOwner() throws Exception {
            when(accountService.getAccountsByOwnerId(1L, PageRequest.of(0, 20)))
                    .thenReturn(new PageImpl<>(List.of(mockAccount())));

            mockMvc.perform(get("/api/v1/accounts/owners/{ownerId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isNotEmpty());

            verify(accountService, times(1))
                    .getAccountsByOwnerId(1L, PageRequest.of(0, 20));
        }
    }

    @Nested
    class GetAllAccounts {

        @DisplayName("return accounts")
        @Test
        public void returnValidAccounts() throws Exception {
            when(accountService.getAllAccounts(PageRequest.of(0, 20)))
                    .thenReturn(new PageImpl<>(List.of(mockAccount())));

            mockMvc.perform(get("/api/v1/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isNotEmpty());

            verify(accountService, times(1))
                    .getAllAccounts(PageRequest.of(0, 20));
        }
    }

    @Nested
    class WithdrawTests {

        @DisplayName("throw error, id is string")
        @Test
        public void throwErrorIdIsString() throws Exception {
            final var operationDto = mockOperationDto();

            mockMvc.perform(put("/api/v1/accounts/{accountId}/withdraw", "test")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency is null")
        @Test
        public void throwErrorCurrencyIsNull() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setCurrency(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/withdraw", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency cannot be blank")
        @Test
        public void throwErrorCurrencyIsBlank() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setCurrency(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/withdraw", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, amount cannot be null")
        @Test
        public void throwErrorAmountIsNull() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setAmount(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/withdraw", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, amount cannot be negative")
        @Test
        public void throwErrorAmountIsNegative() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setAmount(-1D);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/withdraw", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("valid account withdraw")
        @Test
        public void validWithdraw() throws Exception {
            final var operationDto = mockOperationDto();
            final var account = mockAccount();
            when(accountService.withdraw(account.getId(), operationDto))
                    .thenReturn(account);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/withdraw", account.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(account.getId()));

            verify(accountService, times(1))
                    .withdraw(account.getId(), operationDto);
        }
    }

    @Nested
    class DepositTests {

        @DisplayName("throw error, id is string")
        @Test
        public void throwErrorIdIsString() throws Exception {
            final var operationDto = mockOperationDto();

            mockMvc.perform(put("/api/v1/accounts/{accountId}/deposit", "test")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency is null")
        @Test
        public void throwErrorCurrencyIsNull() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setCurrency(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/deposit", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency cannot be blank")
        @Test
        public void throwErrorCurrencyIsBlank() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setCurrency(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/deposit", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, amount cannot be null")
        @Test
        public void throwErrorAmountIsNull() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setAmount(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/deposit", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, amount cannot be negative")
        @Test
        public void throwErrorAmountIsNegative() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setAmount(-1D);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/deposit", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("valid account deposit")
        @Test
        public void validWithdraw() throws Exception {
            final var operationDto = mockOperationDto();
            final var account = mockAccount();
            when(accountService.deposit(account.getId(), operationDto))
                    .thenReturn(account);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/deposit", account.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(account.getId()));

            verify(accountService, times(1))
                    .deposit(account.getId(), operationDto);
        }
    }

    @Nested
    class WireTransferTests {

        @DisplayName("throw error, id cannot be string")
        @Test
        public void throwErrorIdIsString() throws Exception {
            final var operationDto = mockOperationDto();

            mockMvc.perform(put("/api/v1/accounts/{accountId}/wire-transfer/{toAccountId}", "test", 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency is null")
        @Test
        public void throwErrorCurrencyIsNull() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setCurrency(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/wire-transfer/{toAccountId}", 1L, 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, currency cannot be blank")
        @Test
        public void throwErrorCurrencyIsBlank() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setCurrency(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/wire-transfer/{toAccountId}", 1L, 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, amount cannot be null")
        @Test
        public void throwErrorAmountIsNull() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setAmount(null);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/wire-transfer/{toAccountId}", 1L, 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("throw error, amount cannot be negative")
        @Test
        public void throwErrorAmountIsNegative() throws Exception {
            final var operationDto = mockOperationDto();
            operationDto.setAmount(-1D);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/wire-transfer/{toAccountId}", 1L, 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isBadRequest());

        }

        @DisplayName("valid account wire transfer")
        @Test
        public void validWithdraw() throws Exception {
            final var operationDto = mockOperationDto();
            final var account = mockAccount();
            when(accountService.wireTransfer(account.getId(), 2L, operationDto))
                    .thenReturn(account);

            mockMvc.perform(put("/api/v1/accounts/{accountId}/wire-transfer/{toAccountId}", account.getId(), 2L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonConvertor.toJson(operationDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(account.getId()));

            verify(accountService, times(1))
                    .wireTransfer(account.getId(), 2L, operationDto);
        }
    }
}

